package org.openhab.binding.heos.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

    public HeosResponse parseResult(String string) {
        // Debug!!!
        System.out.println(string);
        this.eventResponse = gson.fromJson(string, HeosResponseEvent.class);
        this.payloadResponse = gson.fromJson(string, HeosResponsePayload.class);

        this.response.setEvent(eventResponse);
        this.response.setPayload(payloadResponse);

        // Some times the messages get mixed up and additional informations are added to the pid Message.
        // This is just a simple check routine which checks if the pid is bigger than 9 chars.
        // Setting the pid to 0 can be used to check of message failed during further investigation

        if (eventResponse.getMessagesMap().containsKey("pid")) {
            if (eventResponse.getMessagesMap().get("pid").length() > 9) {
                response.setPid("0");
                return response;
            }
            response.setPid((eventResponse.getMessagesMap().get("pid")));
        }

        return response;
    }
}
