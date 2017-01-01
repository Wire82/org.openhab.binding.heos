package org.openhab.binding.heos.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HeosResponsePayload {

    private List<HashMap<String, String>> payload = new ArrayList<HashMap<String, String>>();
    private List<HashMap<String, String>> player = new ArrayList<HashMap<String, String>>();

    @Override
    public String toString() {
        return payloadToString();
    }

    private String payloadToString() {

        String returnString = "";

        for (int i = 0; i < payload.size(); i++) {

            returnString = returnString + "\n\nPayload: " + (i + 1);

            for (String key : payload.get(i).keySet()) {
                returnString = returnString + "\n" + key + ":\t " + payload.get(i).get(key);
            }
        }

        return returnString;

    }

    public List<HashMap<String, String>> getPayloadList() {
        return payload;
    }

    public void setPayload(List<HashMap<String, String>> payload) {
        this.payload = payload;
    }

    public List<HashMap<String, String>> getPlayerList() {
        return player;
    }

    public void setPlayerList(List<HashMap<String, String>> player) {
        this.player = player;

    }

}
