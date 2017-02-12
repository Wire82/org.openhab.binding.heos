package org.openhab.binding.heos.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HeosResponsePayload {

    private List<HashMap<String, String>> payload = new ArrayList<HashMap<String, String>>();
    private List<List<HashMap<String, String>>> groupMembers = new ArrayList<List<HashMap<String, String>>>();

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

    /**
     * This returns a list with HashMaps which contain the
     * single pairs of information.
     * Each HashMap represent a single JSON Array which was
     * received by the HEOS response. For details for the
     * expected response revere to the HEOS specification
     *
     * @return a list with HashMaps for each JSON Array
     */

    public List<HashMap<String, String>> getPayloadList() {
        return payload;
    }

    public void setPayload(List<HashMap<String, String>> payload) {
        this.payload = payload;
    }

    /**
     * This returns a list with one element for each group.
     * Each of this elements contain again a list with one
     * element for a each player which is part of the group.
     * This information is received by the get_groups command.
     * The HashMap within the last list represents the player
     * with its informations
     *
     * @return nested Lists for the groups and their members
     */

    public List<List<HashMap<String, String>>> getPlayerList() {
        return groupMembers;
    }

    public void setPlayerList(List<List<HashMap<String, String>>> player) {
        this.groupMembers = player;

    }

}
