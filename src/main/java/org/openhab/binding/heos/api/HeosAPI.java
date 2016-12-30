package org.openhab.binding.heos.api;

import org.openhab.binding.heos.resources.HeosEventListener;

public class HeosAPI {

    private HeosSystem controller = null;
    private HeosEventController event = null;

    public HeosAPI(HeosSystem controller, HeosEventController event) {

        this.controller = controller;
        this.event = event;
    }

    public void pause(String pid) {

        controller.send(controller.command().setPlayStatePause(pid));

    }

    public void play(String pid) {

        controller.send(controller.command().setPlayStatePlay(pid));

    }

    public void stop(String pid) {

        controller.send(controller.command().setPlayStateStop(pid));

    }

    public void next(String pid) {

        controller.send(controller.command().playNext(pid));

    }

    public void prevoious(String pid) {

        controller.send(controller.command().playPrevious(pid));

    }

    public void mute(String pid) {

        controller.send(controller.command().setMuteToggle(pid));
    }

    public void muteON(String pid) {

        controller.send(controller.command().setMuteOn(pid));

    }

    public void muteOFF(String pid) {

        controller.send(controller.command().setMuteOff(pid));

    }

    public void volume(String vol, String pid) {

        controller.send(controller.command().setVolume(vol, pid));

    }

    public void setHeosConnection(String ip, int port) {

        controller.setConnectionIP(ip);
        controller.setConnectionPort(port);
        controller.establishConnection();

    }

    public void setUserDate(String name, String password) {

        controller.command().setUsernamePwassword(name, password);

    }

    public void setActivePlayer(String playerID) {

        controller.command().setPlayerID(playerID);

    }

    public void registerforChangeEvents(HeosEventListener listener) {

        event.addListener(listener);

    }

}
