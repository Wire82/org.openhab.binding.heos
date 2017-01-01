package org.openhab.binding.heos.resources;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class HeosDeserializerPayload implements JsonDeserializer<HeosResponsePayload> {

    // Debug: Return value of PLayerList has to be defined if no player found

    private HeosResponsePayload responsePayload = new HeosResponsePayload();

    @Override
    public HeosResponsePayload deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        boolean arrayTrue = false;
        List<HashMap<String, String>> mapList = new ArrayList<HashMap<String, String>>();
        List<HashMap<String, String>> playerList = new ArrayList<HashMap<String, String>>();

        JsonObject jsonObject = json.getAsJsonObject();

        if (jsonObject.has("payload")) {
            if (jsonObject.get("payload").isJsonArray()) {

                arrayTrue = true;
            }

        }

        if (jsonObject.has("payload") && arrayTrue) {

            JsonArray jsonArray = jsonObject.get("payload").getAsJsonArray();

            for (int i = 0; i < jsonArray.size(); i++) {
                HashMap<String, String> payload = new HashMap<String, String>();

                JsonObject object = jsonArray.get(i).getAsJsonObject();

                for (Entry<String, JsonElement> entry : object.entrySet()) {

                    if (entry.getValue().isJsonArray()) {
                        JsonArray playerArray = entry.getValue().getAsJsonArray();
                        for (int j = 0; j < playerArray.size(); j++) {

                            HashMap<String, String> player = new HashMap<String, String>();
                            JsonObject playerObj = playerArray.get(j).getAsJsonObject();

                            for (Entry<String, JsonElement> element : playerObj.entrySet()) {

                                player.put(element.getKey(), element.getValue().getAsString());

                            }
                            playerList.add(player);
                        }

                    } else {
                        payload.put(entry.getKey(), entry.getValue().getAsString());

                        // Debug
                        // System.out.println(entry.getKey() + ": " + entry.getValue());
                    }

                }

                mapList.add(payload);

            }

        } else if (jsonObject.has("payload") && !arrayTrue)

        {
            HashMap<String, String> payload = new HashMap<String, String>();
            JsonObject jsonPayload = jsonObject.get("payload").getAsJsonObject();

            for (Entry<String, JsonElement> entry : jsonPayload.entrySet()) {
                if (entry.getValue().isJsonArray()) {
                    JsonArray playerArray = entry.getValue().getAsJsonArray();
                    for (int j = 0; j < playerArray.size(); j++) {

                        HashMap<String, String> player = new HashMap<String, String>();
                        JsonObject playerObj = playerArray.get(j).getAsJsonObject();

                        for (Entry<String, JsonElement> element : playerObj.entrySet()) {

                            player.put(element.getKey(), element.getValue().getAsString());
                        }
                        playerList.add(player);
                    }

                } else {

                    payload.put(entry.getKey(), entry.getValue().getAsString());
                    // System.out.println(entry.getKey()+ ": " + entry.getValue());
                }

            }
            mapList.add(payload);

        } else {
            HashMap<String, String> player = new HashMap<String, String>();
            HashMap<String, String> payload = new HashMap<String, String>();
            payload.put("No Payload", "No Payload");
            player.put("No Player", "No Player");
            playerList.add(player);
            mapList.add(payload);
        }

        responsePayload.setPlayerList(playerList);
        responsePayload.setPayload(mapList);
        return responsePayload;
    }

    public void itterateValues() {

    }
}
