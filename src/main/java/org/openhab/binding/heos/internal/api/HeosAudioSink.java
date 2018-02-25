/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openhab.binding.heos.internal.api;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.eclipse.smarthome.core.audio.AudioFormat;
import org.eclipse.smarthome.core.audio.AudioHTTPServer;
import org.eclipse.smarthome.core.audio.AudioSink;
import org.eclipse.smarthome.core.audio.AudioStream;
import org.eclipse.smarthome.core.audio.FileAudioStream;
import org.eclipse.smarthome.core.audio.FixedLengthAudioStream;
import org.eclipse.smarthome.core.audio.URLAudioStream;
import org.eclipse.smarthome.core.audio.UnsupportedAudioFormatException;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.thing.util.ThingHandlerHelper;
import org.openhab.binding.heos.handler.HeosThingBaseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This makes HEOS to serve as an {@link AudioSink}-
 *
 * @author Kai Kreuzer - Initial contribution and API
 * @author Johannes Einig - Adapted for HEOS
 *
 */

public class HeosAudioSink implements AudioSink {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final HashSet<AudioFormat> SUPPORTED_AUDIO_FORMATS = new HashSet<>();
    private static final HashSet<Class<? extends AudioStream>> SUPPORTED_AUDIO_STREAMS = new HashSet<>();

    static {
        SUPPORTED_AUDIO_FORMATS.add(AudioFormat.WAV);
        SUPPORTED_AUDIO_FORMATS.add(AudioFormat.MP3);
        SUPPORTED_AUDIO_FORMATS.add(AudioFormat.AAC);

        SUPPORTED_AUDIO_STREAMS.add(URLAudioStream.class);
        SUPPORTED_AUDIO_STREAMS.add(FixedLengthAudioStream.class);
    }

    private AudioHTTPServer audioHTTPServer;
    private HeosThingBaseHandler handler;
    private String callbackUrl;

    public HeosAudioSink(HeosThingBaseHandler handler, AudioHTTPServer audioHTTPServer, String callbackUrl) {
        this.handler = handler;
        this.audioHTTPServer = audioHTTPServer;
        this.callbackUrl = callbackUrl;
    }

    @Override
    public String getId() {
        return handler.getThing().getUID().toString();
    }

    @Override
    public String getLabel(Locale locale) {
        return handler.getThing().getLabel();
    }

    @Override
    public void process(AudioStream audioStream) throws UnsupportedAudioFormatException {
        String url = null;
        if (audioStream instanceof URLAudioStream) {
            // it is an external URL, the speaker can access it itself and play it.
            URLAudioStream urlAudioStream = (URLAudioStream) audioStream;
            url = urlAudioStream.getURL();
            handler.playURL(url);
        } else if (audioStream instanceof FixedLengthAudioStream) {
            if (callbackUrl != null) {
                // we serve it on our own HTTP server for 30 seconds as HEOS requests the stream several times
                String relativeUrl = audioHTTPServer.serve((FixedLengthAudioStream) audioStream, 30).toString();
                url = callbackUrl + relativeUrl;

                AudioFormat audioFormat = audioStream.getFormat();
                if (!ThingHandlerHelper.isHandlerInitialized(handler)) {
                    logger.warn("HEOS speaker '{}' is not initialized - status is {}", handler.getThing().getUID(),
                            handler.getThing().getStatus());
                } else if (AudioFormat.MP3.isCompatible(audioFormat)) {
                    handler.playURL(url + FileAudioStream.MP3_EXTENSION);
                } else if (AudioFormat.WAV.isCompatible(audioFormat)) {
                    handler.playURL(url + FileAudioStream.WAV_EXTENSION);
                } else if (AudioFormat.WAV.isCompatible(audioFormat)) {
                    handler.playURL(url + FileAudioStream.AAC_EXTENSION);
                } else {
                    throw new UnsupportedAudioFormatException("HEOS only supports MP3 or WAV.", audioFormat);
                }
            } else {
                logger.warn("We do not have any callback url, so HEOS cannot play the audio stream!");
                return;
            }
        } else {
            throw new UnsupportedAudioFormatException("HEOS can only handle FixedLengthAudioStreams.", null);
        }
    }

    @Override
    public Set<AudioFormat> getSupportedFormats() {
        return SUPPORTED_AUDIO_FORMATS;
    }

    @Override
    public Set<Class<? extends AudioStream>> getSupportedStreams() {
        return SUPPORTED_AUDIO_STREAMS;
    }

    @Override
    public PercentType getVolume() {
        return handler.getNotificationSoundVolume();
    }

    @Override
    public void setVolume(PercentType volume) {
        handler.setNotificationSoundVolume(volume);
    }
}
