package org.openhab.binding.heos.api;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;

import org.openhab.binding.heos.resources.HeosCommands;
import org.openhab.binding.heos.resources.HeosGroup;
import org.openhab.binding.heos.resources.HeosJsonParser;
import org.openhab.binding.heos.resources.HeosPlayer;
import org.openhab.binding.heos.resources.HeosResponse;
import org.openhab.binding.heos.resources.HeosSendCommand;
import org.openhab.binding.heos.resources.Telnet;

public class HeosSystem {

    private String connectionIP = "";
    private int connectionPort = 0;

    private Telnet commandLine;
    private Telnet eventLine;
    private HeosCommands heosCommand = new HeosCommands();
    private HeosResponse response = new HeosResponse();
    private HeosJsonParser parser = new HeosJsonParser(response);
    private HeosEventController eventController = new HeosEventController(response, heosCommand, this);
    private HeosSendCommand sendCommand = new HeosSendCommand(commandLine, parser, response, eventController);
    HashMap<String, HeosPlayer> playerMapNew;
    HashMap<String, HeosGroup> groupMapNew;
    HashMap<String, HeosPlayer> playerMapOld;
    HashMap<String, HeosGroup> groupMapOld;
    HashMap<String, HeosGroup> removedGroupMap;
    private HeosAPI heosApi = new HeosAPI(this, eventController);

    public HeosSystem() {

    }

    public boolean send(String command) {

        return sendCommand.send(command);

    }

    public HeosCommands command() {

        return heosCommand;
    }

    public boolean establishConnection() {
        this.playerMapNew = new HashMap<String, HeosPlayer>();
        this.groupMapNew = new HashMap<String, HeosGroup>();
        this.playerMapOld = new HashMap<String, HeosPlayer>();
        this.groupMapOld = new HashMap<String, HeosGroup>();
        this.removedGroupMap = new HashMap<String, HeosGroup>();
        this.commandLine = new Telnet();
        this.eventLine = new Telnet();

        // Debug
        System.out.println("Debug: Command Line Connected: " + commandLine.connect(connectionIP, connectionPort));
        sendCommand.setTelnetClient(commandLine);
        send(command().registerChangeEventOFF());
        System.out.println("Debug: Event Line Connected " + eventLine.connect(connectionIP, connectionPort));

        if (commandLine.isConnected() && eventLine.isConnected()) {
            return true;
        }

        return false;
    }

    public void startEventListener() {
        sendCommand.setTelnetClient(eventLine);
        send(command().registerChangeEventOn());
        eventLine.startInputListener();
        sendCommand.setTelnetClient(commandLine);
        eventLine.getReadResultListener().addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                parser.parseResult((String) evt.getNewValue());
                eventController.handleEvent();

            }
        });
    }

    public void closeConnection() {

        eventLine.stopInputListener();
        sendCommand.setTelnetClient(eventLine);
        send(command().registerChangeEventOFF());
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        sendCommand.setTelnetClient(commandLine);
        eventLine.disconnect();
        commandLine.disconnect();

    }

    public HashMap<String, HeosPlayer> getPlayer() {

        boolean resultEmpty = true;

        while (resultEmpty) {
            send(command().getPlayers());
            resultEmpty = response.getPayload().getPayloadList().isEmpty();
            // Debug
            System.out.println("Is EMPTY!!!!");
        }

        List<HashMap<String, String>> playerList = response.getPayload().getPayloadList();

        for (HashMap<String, String> player : playerList) {
            HeosPlayer tempPlay = new HeosPlayer();
            tempPlay.updatePlayerInfo(player);
            tempPlay = updatePlayerState(tempPlay);
            playerMapNew.put(tempPlay.getPid(), tempPlay);

        }

        return playerMapNew;

    }

    private HeosPlayer updatePlayerState(HeosPlayer player) {

        String pid = player.getPid();
        send(command().getPlayState(pid));
        player.setState(response.getEvent().getMessagesMap().get("state"));
        send(command().getMute(pid));
        player.setMute(response.getEvent().getMessagesMap().get("state"));
        send(command().getVolume(pid));
        player.setLevel(response.getEvent().getMessagesMap().get("level"));
        send(command().getNowPlayingMedia(pid));
        player.updateMediaInfo(response.getPayload().getPayloadList().get(0));

        return player;
    }

    public HashMap<String, HeosGroup> getGroups() {

        send(command().getGroups());

        System.out.println("Found: " + response.getPayload().getPlayerList().size() + " Player in Group");
        if (response.getPayload().getPayloadList().isEmpty()) {
            groupMapNew.clear();
            removedGroupMap = compareMaps(groupMapNew, groupMapOld);
            for (String key : groupMapNew.keySet()) {
                groupMapOld.put(key, groupMapNew.get(key));
            }
            return groupMapNew;
        }

        List<HashMap<String, String>> groupList = response.getPayload().getPayloadList();

        for (HashMap<String, String> group : groupList) {
            HeosGroup tempGroup = new HeosGroup();
            tempGroup.updateGroupInfo(group);
            tempGroup = updateGroupState(tempGroup);
            groupMapNew.put(tempGroup.getGid(), tempGroup);
            removedGroupMap = compareMaps(groupMapNew, groupMapOld);
            for (String key : groupMapNew.keySet()) {
                groupMapOld.put(key, groupMapNew.get(key));
            }
        }

        return groupMapNew;

    }

    private HeosGroup updateGroupState(HeosGroup group) {

        String gid = group.getGid();
        send(command().getGroupInfo(gid));
        int playerCount = response.getPayload().getPlayerList().size();

        // Defining the Group leader

        for (int i = 0; i < playerCount; i++) {
            HashMap<String, String> player = new HashMap<>();
            player = response.getPayload().getPlayerList().get(i);
            for (String key : player.keySet()) {
                if (key.equals("role")) {
                    if (player.get(key).equals("leader")) {
                        String leader = player.get("pid");
                        group.setLeader(leader);
                    }
                }
            }
        }

        send(command().getPlayState(group.getLeader()));
        group.setState(response.getEvent().getMessagesMap().get("state"));
        send(command().getGroupMute(gid));
        group.setMute(response.getEvent().getMessagesMap().get("state"));
        send(command().getGroupVolume(gid));
        group.setLevel(response.getEvent().getMessagesMap().get("level"));
        send(command().getNowPlayingMedia(gid));
        group.updateMediaInfo(response.getPayload().getPayloadList().get(0));

        return group;
    }

    private HashMap<String, HeosGroup> compareMaps(HashMap<String, HeosGroup> mapNew,
            HashMap<String, HeosGroup> mapOld) {

        HashMap<String, HeosGroup> removedItems = new HashMap<String, HeosGroup>();
        for (String key : mapOld.keySet()) {
            if (!mapNew.containsKey(key)) {
                removedItems.put(key, mapOld.get(key));
            }

        }

        return removedItems;
    }

    public HeosAPI getAPI() {
        return heosApi;
    }

    public String getConnectionIP() {
        return connectionIP;
    }

    public void setConnectionIP(String connectionIP) {
        this.connectionIP = connectionIP;
    }

    public int getConnectionPort() {
        return connectionPort;
    }

    public void setConnectionPort(int connectionPort) {
        this.connectionPort = connectionPort;
    }

    public HashMap<String, HeosPlayer> getPlayerMap() {
        return playerMapNew;
    }

    public HashMap<String, HeosGroup> getGroupMap() {
        return groupMapNew;
    }

    public HashMap<String, HeosGroup> getGroupsRemoved() {
        return removedGroupMap;
    }

}
