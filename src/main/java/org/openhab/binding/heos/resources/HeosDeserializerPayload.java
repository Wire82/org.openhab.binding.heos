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

    private HeosResponsePayload responsePayload = new HeosResponsePayload();

    @Override
    public HeosResponsePayload deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        boolean arrayTrue = false;
        List<HashMap<String, String>> mapList = new ArrayList<HashMap<String, String>>();

        JsonObject jsonObject = json.getAsJsonObject();

        // Checks if behind "payload" an array starts or not. Feature might be implemented much better...... :)
        String check = String.valueOf(json);
        int payPor = check.indexOf("payload");
        // jsonObject.get("payload").isArray(); Maybe use this one.....
        if (String.valueOf(check.charAt(payPor + 9)).equals("[")) {
            arrayTrue = true;
        }

        if (jsonObject.has("payload") && arrayTrue) {

            JsonArray jsonArray = jsonObject.get("payload").getAsJsonArray();

            for (int i = 0; i < jsonArray.size(); i++) {
                HashMap<String, String> payload = new HashMap<String, String>();
                JsonObject object = jsonArray.get(i).getAsJsonObject();
                for (Entry<String, JsonElement> entry : object.entrySet()) {
                    payload.put(entry.getKey(), entry.getValue().getAsString());
                    // System.out.println(entry.getKey()+ ": " + entry.getValue());
                }
                mapList.add(payload);
            }
        } else if (jsonObject.has("payload") && !jsonObject.isJsonArray()) {
            HashMap<String, String> payload = new HashMap<String, String>();
            JsonObject jsonPayload = jsonObject.get("payload").getAsJsonObject();

            for (Entry<String, JsonElement> entry : jsonPayload.entrySet()) {

                payload.put(entry.getKey(), entry.getValue().getAsString());
                // System.out.println(entry.getKey()+ ": " + entry.getValue());
            }
            mapList.add(payload);

        } else {
            HashMap<String, String> payload = new HashMap<String, String>();
            payload.put("No Payload", "No Payload");
            mapList.add(payload);
        }
        responsePayload.setPayload(mapList);
        return responsePayload;
    }

    public void itterateValues() {

    }
}
