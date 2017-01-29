package org.openhab.binding.heos.resources;

import org.openhab.binding.heos.api.HeosEventController;

public class HeosSendCommand {

    private Telnet client;
    private HeosJsonParser parser;
    private HeosResponse response;
    private HeosEventController eventController;

    private String command = "";

    public HeosSendCommand(Telnet client, HeosJsonParser parser, HeosResponse response,
            HeosEventController eventController) {

        this.client = client;
        this.parser = parser;
        this.response = response;
        this.eventController = eventController;
    }

    public synchronized boolean send(String command) {
        int sendTryCounter = 0;
        this.command = command;

        if (executeSendCommand()) {
            while (sendTryCounter < 4) {
                if (response.getEvent().getResult().equals("fail")) {
                    executeSendCommand();
                    sendTryCounter++;
                } else if (response.getEvent().getMessagesMap().get("command under process").equals("true")) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    parser.parseResult(client.readLine());
                } else {
                    return true;
                }
            }
            ;
            return true;
        } else {
            return false;
        }

    }

    public boolean sendWithoutResponse(String command) {
        return client.send(command);
        // Debug
        // System.out.println(command);
    }

    private boolean executeSendCommand() {
        // Debug
        System.out.println("Sending Command: " + command);
        boolean sendSuccess = client.send(command);
        if (sendSuccess) {
            parser.parseResult(client.readLine());
            eventController.handleEvent();
            return true;
        } else {
            return false;
        }

    }

    public void setTelnetClient(Telnet client) {
        this.client = client;
    }

}
