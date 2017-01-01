package org.openhab.binding.heos.resources;

import java.util.HashMap;

public class HeosGroup extends HeosMediaObject {

    private final String[] supportedGroupInfo = { "name", "gip", "leader" };
    private final String[] supportedGroupStates = { "state", "level", "mute" };

    private HashMap<String, String> groupInfo;
    private HashMap<String, String> groupState;

    // Group Infos Variables
    private String name;
    private String gid;
    private String leader;

    // Group State Variables
    private String state;
    private String level;
    private String mute;

    public HeosGroup() {

        initGroup();
    }

    public void updateGroupInfo(HashMap<String, String> values) {

        groupInfo = values;
        for (String key : values.keySet()) {
            if (key.equals("name")) {
                name = values.get(key);
            }
            if (key.equals("leader")) {
                leader = values.get(key);
            }
            if (key.equals("gid")) {
                gid = values.get(key);
            }
        }

    }

    public void updateGroupState(HashMap<String, String> values) {

        groupState = values;
        for (String key : values.keySet()) {
            if (key.equals("state")) {
                state = values.get(key);
            }
            if (key.equals("level")) {
                level = values.get(key);
            }
            if (key.equals("mute")) {
                mute = values.get(key);
            }

        }

    }

    private void initGroup() {

        groupInfo = new HashMap<>(8);
        groupState = new HashMap<>(5);

        for (String key : supportedGroupInfo) {
            groupInfo.put(key, null);
        }

        for (String key : supportedGroupStates) {
            groupState.put(key, null);
        }

        updateGroupInfo(groupInfo);
        updateGroupState(groupState);

    }

    public HashMap<String, String> getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(HashMap<String, String> groupInfo) {
        this.groupInfo = groupInfo;
    }

    public HashMap<String, String> getGroupState() {
        return groupState;
    }

    public void setGroupState(HashMap<String, String> groupState) {
        this.groupState = groupState;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        groupInfo.put("name", name);
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
        groupInfo.put("gid", gid);
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
        groupInfo.put("leader", leader);
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
        groupInfo.put("state", state);
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
        groupInfo.put("level", level);
    }

    public String getMute() {
        return mute;
    }

    public void setMute(String mute) {
        this.mute = mute;
        groupInfo.put("mute", mute);
    }

    public String[] getSupportedGroupInfo() {
        return supportedGroupInfo;
    }

    public String[] getSupportedGroupStates() {
        return supportedGroupStates;
    }

}
