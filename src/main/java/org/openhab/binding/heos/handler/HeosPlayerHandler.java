package org.openhab.binding.heos.handler;

import static org.openhab.binding.heos.HeosBindingConstants.*;
import static org.openhab.binding.heos.resources.HeosConstants.PID;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.library.types.PlayPauseType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.heos.api.HeosAPI;
import org.openhab.binding.heos.api.HeosSystem;
import org.openhab.binding.heos.resources.HeosEventListener;
import org.openhab.binding.heos.resources.HeosPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeosPlayerHandler extends BaseThingHandler implements HeosEventListener {

    private HeosAPI api;
    private HeosSystem heos;
    private String pid;
    private HashMap<String, HeosPlayer> playerMap;
    private HeosPlayer player;

    private Logger logger = LoggerFactory.getLogger(HeosPlayerHandler.class);

    public HeosPlayerHandler(Thing thing, HeosSystem heos, HeosAPI api) {
        super(thing);
        this.heos = heos;
        this.api = api;
        pid = thing.getConfiguration().get(PID).toString();

    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        if (command.toString().equals("REFRESH")) {
            return;
        }

        if (channelUID.getId().equals(CH_ID_CONTROL)) {

            String com = command.toString();

            switch (com) {

                case "PLAY":
                    api.play(pid);
                    break;
                case "PAUSE":
                    api.pause(pid);
                    break;
                case "NEXT":
                    api.next(pid);
                    break;
                case "PREVIOUS":
                    api.prevoious(pid);
                    break;
                case "ON":
                    api.play(pid);
                    break;
                case "OFF":
                    api.pause(pid);
                    break;

            }
        } else if (channelUID.getId().equals(CH_ID_VOLUME)) {

            api.volume(command.toString(), pid);

        } else if (channelUID.getId().equals(CH_ID_MUTE)) {

            if (command.toString().equals("ON")) {
                api.muteON(pid);
            } else {
                api.muteOFF(pid);
            }
        } else if (channelUID.getId().equals(CH_ID_INPUTS)) {

            api.playInputSource(pid, command.toString());

        }

    }

    @Override
    public void initialize() {

        api.registerforChangeEvents(this);
        ScheduledExecutorService executerPool = Executors.newScheduledThreadPool(1);
        executerPool.schedule(new InitializationRunnable(), 3, TimeUnit.SECONDS);
        updateStatus(ThingStatus.ONLINE);
        super.initialize();

    }

    @Override
    public void dispose() {
        api.unregisterforChangeEvents(this);

    }

    @Override
    public void playerStateChangeEvent(String pid, String event, String command) {

        if (pid.equals(this.pid)) {
            if (event.equals(STATE)) {
                switch (command) {

                    case PLAY:
                        updateState(CH_ID_CONTROL, PlayPauseType.PLAY);
                        break;
                    case PAUSE:
                        updateState(CH_ID_CONTROL, PlayPauseType.PAUSE);
                        break;
                    case STOP:
                        updateState(CH_ID_CONTROL, PlayPauseType.PAUSE);
                        break;
                }

            }
            if (event.equals(VOLUME)) {

                updateState(CH_ID_VOLUME, PercentType.valueOf(command));

            }
            if (event.equals(MUTE)) {
                if (command != null) {
                    switch (command) {
                        case ON:
                            updateState(CH_ID_MUTE, OnOffType.ON);
                            break;
                        case OFF:
                            updateState(CH_ID_MUTE, OnOffType.OFF);
                            break;
                    }
                }

            }

        }

    }

    @Override
    public void playerMediaChangeEvent(String pid, HashMap<String, String> info) {

        if (pid.equals(this.pid)) {
            for (String key : info.keySet()) {

                switch (key) {

                    case SONG:
                        updateState(CH_ID_SONG, StringType.valueOf(info.get(key)));
                        break;
                    case ARTIST:
                        updateState(CH_ID_ARTIST, StringType.valueOf(info.get(key)));
                        break;
                    case ALBUM:
                        updateState(CH_ID_ALBUM, StringType.valueOf(info.get(key)));
                        break;
                    case IMAGE_URL:
                        updateState(CH_ID_IMAGE_URL, StringType.valueOf(info.get(key)));
                        break;

                }

            }
        }

    }

    @Override
    public void bridgeChangeEvent(String event, String result, String command) {
        // TODO Auto-generated method stub

    }

    public class InitializationRunnable implements Runnable {

        @Override
        public void run() {

            player = heos.getPlayerState(pid);

            if (player.getLevel() != null) {
                updateState(CH_ID_VOLUME, PercentType.valueOf(player.getLevel()));
            }

            if (player.getMute().equals(ON)) {
                updateState(CH_ID_MUTE, OnOffType.ON);
            } else {
                updateState(CH_ID_MUTE, OnOffType.OFF);
            }

            if (player.getState().equals(PLAY)) {
                updateState(CH_ID_CONTROL, PlayPauseType.PLAY);
            }
            if (player.getState().equals(PAUSE) || player.getState().equals(STOP)) {
                updateState(CH_ID_CONTROL, PlayPauseType.PAUSE);
            }
            updateState(CH_ID_SONG, StringType.valueOf(player.getSong()));
            updateState(CH_ID_ARTIST, StringType.valueOf(player.getArtist()));
            updateState(CH_ID_ALBUM, StringType.valueOf(player.getAlbum()));
            updateState(CH_ID_IMAGE_URL, StringType.valueOf(player.getImage_url()));
            updateState(CH_ID_INPUTS, StringType.valueOf("NULL"));

        }

    }
}
