package org.openhab.binding.heos.api;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;

import org.openhab.binding.heos.resources.HeosCommands;
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
    HashMap<String, HeosPlayer> playerMap;
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
        this.playerMap = new HashMap<String, HeosPlayer>();
        this.commandLine = new Telnet();
        this.eventLine = new Telnet();

        // Debug
        System.out.println("Debug: Command Line Connected: " + commandLine.connect(connectionIP, connectionPort));
        sendCommand.setTelnetClient(commandLine);
        send(command().registerChangeEventOFF());
        getPlayer();
        System.out.println("Debug: Event Line Connected " + eventLine.connect(connectionIP, connectionPort));
        eventAction();

        if (commandLine.isConnected() && eventLine.isConnected()) {
            return true;
        }

        return false;
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
        }

        List<HashMap<String, String>> playerList = response.getPayload().getPayloadList();

        for (HashMap<String, String> player : playerList) {
            HeosPlayer tempPlay = new HeosPlayer();
            tempPlay.updatePlayerInfo(player);
            tempPlay = updatePlayerState(tempPlay);
            playerMap.put(tempPlay.getPid(), tempPlay);
        }

        return playerMap;

    }

    private HeosPlayer updatePlayerState(HeosPlayer player) {

        String pid = player.getPid();
        // Debug: pid will only changed after one try... I dont know why....

        send(command().getPlayState(pid));
        player.setState(response.getEvent().getMessagesMap().get("state"));
        send(command().getMute(pid));
        player.setMute(response.getEvent().getMessagesMap().get("state"));
        send(command().getVolume(pid));
        player.setLevel(response.getEvent().getMessagesMap().get("level"));

        return player;
    }

    private void eventAction() {
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
        return playerMap;
    }

}
