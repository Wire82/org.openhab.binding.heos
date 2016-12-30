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

    public boolean send(String command) {
        int sendTryCounter = 0;
        this.command = command;
        executeSendCommand();
        while (sendTryCounter < 4) {
            if (response.getEvent().getResult().equals("fail")) {
                executeSendCommand();
                sendTryCounter++;
            } else if (response.getEvent().getMessagesMap().get("command under process").equals("true")) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // Debug
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                parser.parseResult(client.readLine());
            } else {
                return true;
            }
        }
        ;
        return false;
    }

    public void sendWithoutResponse(String command) {
        client.send(command);
        // Debug
        // System.out.println(command);
    }

    private void executeSendCommand() {

        client.send(command);
        // Debug
        System.out.println("Sending Command: " + command);
        parser.parseResult(client.readLine());
        eventController.handleEvent();

    }

    public void setTelnetClient(Telnet client) {
        this.client = client;
    }

}
