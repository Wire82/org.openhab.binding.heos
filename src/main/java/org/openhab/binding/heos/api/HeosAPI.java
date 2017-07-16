/**
 * Copyright (c) 2014-2017 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.heos.api;

import org.openhab.binding.heos.internal.resources.HeosEventListener;

/**
 * The {@link HeosAPI} is the interface for handling commands, which are
 * sent to the HEOS system.
 *
 * @author Johannes Einig - Initial contribution
 */

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

    public void muteGroup(String pid) {

        controller.send(controller.command().setMuteToggle(pid));
    }

    public void muteGroupON(String pid) {

        controller.send(controller.command().setGroupMuteOn(pid));

    }

    public void muteGroupOFF(String pid) {

        controller.send(controller.command().setGroupMuteOff(pid));

    }

    public void volumeGroup(String vol, String pid) {

        controller.send(controller.command().setGroupVolume(vol, pid));

    }

    public void ungroupGroup(String gid) {
        String[] pid = new String[] { gid };
        controller.send(controller.command().setGroup(pid));
    }

    public void groupPlayer(String[] gid) {

        controller.send(controller.command().setGroup(gid));
    }

    public void browseSource(String sid) {
        controller.send(controller.command().BrowseSource(sid));
    }

    public void addContainerToQueuePlayNow(String pid, String sid, String cid) {
        controller.send(controller.command().addContainerToQueuePlayNow(pid, sid, cid));
    }

    public void setHeosConnection(String ip, int port) {

        controller.setConnectionIP(ip);
        controller.setConnectionPort(port);
        controller.establishConnection(false);

    }

    public void reboot() {
        controller.sendWithoutResponse(controller.command().rebootSystem());
    }

    public void logIn(String name, String password) {

        controller.command().setUsernamePwassword(name, password);
        controller.send(controller.command().signIn(name, password));

    }

    public void playStation(String pid, String sid, String cid, String mid, String name) {

        controller.send(controller.command().playStation(pid, sid, cid, mid, name));

    }

    /**
     * Plays a specified input source on the player.
     * Set {@code source_pid} to null if destination and source is the same
     * Input name as per specified in HEOS CLI Protocol
     *
     * @param des_pid the PID where the source shall be played
     * @param source_pid the PID where the source is located. NULL if destination and source are the same
     * @param input the input name
     */

    public void playInputSource(String des_pid, String source_pid, String input) {

        if (source_pid == null) {
            controller.send(controller.command().playInputSource(des_pid, des_pid, input));
        } else {
            controller.send(controller.command().playInputSource(des_pid, source_pid, input));
        }

    }

    public void setActivePlayer(String playerID) {

        controller.command().setPlayerID(playerID);

    }

    public void registerforChangeEvents(HeosEventListener listener) {

        event.addListener(listener);

    }

    public void unregisterforChangeEvents(HeosEventListener listener) {

        event.removeListener(listener);

    }

}
