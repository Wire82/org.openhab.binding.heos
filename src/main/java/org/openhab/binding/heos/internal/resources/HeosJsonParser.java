/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.heos.internal.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * The {@link HeosJsonParser} parses the JSON message of the
 * Heos bridge to the correct Deserializer
 *
 * @author Johannes Einig - Initial contribution
 */

public class HeosJsonParser {

    private HeosResponse response;
    private HeosResponseEvent eventResponse = null;
    private HeosResponsePayload payloadResponse = null;
    Gson gson = null;

    public HeosJsonParser(HeosResponse response) {
        this.response = response;
        this.eventResponse = response.getEvent();
        this.payloadResponse = response.getPayload();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(HeosResponseEvent.class, new HeosDeserializerEvent());
        gsonBuilder.registerTypeAdapter(HeosResponsePayload.class, new HeosDeserializerPayload());
        this.gson = gsonBuilder.create();
    }

    public synchronized HeosResponse parseResult(String receivedMessage) {
        response.setRawResponseMessage(receivedMessage);

        this.eventResponse = gson.fromJson(receivedMessage, HeosResponseEvent.class);
        this.payloadResponse = gson.fromJson(receivedMessage, HeosResponsePayload.class);

        this.response.setEvent(eventResponse);
        this.response.setPayload(payloadResponse);

        if (eventResponse.getMessagesMap().containsKey("pid")) {
            response.setPid((eventResponse.getMessagesMap().get("pid")));
        }
        return response;
    }
}
