/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.heos.internal.api;

import java.net.URL;

import org.openhab.binding.heos.internal.resources.HeosEventListener;

/**
 * The {@link HeosFacade} is the interface for handling commands, which are
 * sent to the HEOS system.
 *
 * @author Johannes Einig - Initial contribution
 */

public class HeosFacade {

    private HeosSystem controller = null;
    private HeosEventController event = null;

    public HeosFacade(HeosSystem controller, HeosEventController event) {
        this.controller = controller;
        this.event = event;
    }

    /**
     * Pauses the HEOS player
     *
     * @param pid The PID of the dedicated player
     */
    public void pause(String pid) {
        controller.send(controller.command().setPlayStatePause(pid));
    }

    /**
     * Starts the HEOS player
     *
     * @param pid The PID of the dedicated player
     */
    public void play(String pid) {
        controller.send(controller.command().setPlayStatePlay(pid));
    }

    /**
     * Stops the HEOS player
     *
     * @param pid The PID of the dedicated player
     */
    public void stop(String pid) {
        controller.send(controller.command().setPlayStateStop(pid));
    }

    /**
     * Jumps to the next song on the HEOS player
     *
     * @param pid The PID of the dedicated player
     */
    public void next(String pid) {
        controller.send(controller.command().playNext(pid));
    }

    /**
     * Jumps to the previous song on the HEOS player
     *
     * @param pid The PID of the dedicated player
     */
    public void previous(String pid) {
        controller.send(controller.command().playPrevious(pid));
    }

    /**
     * Toggles the mute state the HEOS player
     *
     * @param pid The PID of the dedicated player
     */
    public void mute(String pid) {
        controller.send(controller.command().setMuteToggle(pid));
    }

    /**
     * Mutes the HEOS player
     *
     * @param pid The PID of the dedicated player
     */
    public void muteON(String pid) {
        controller.send(controller.command().setMuteOn(pid));
    }

    /**
     * Un-mutes the HEOS player
     *
     * @param pid The PID of the dedicated player
     */
    public void muteOFF(String pid) {
        controller.send(controller.command().setMuteOff(pid));
    }

    /**
     * Set the HEOS player to a dedicated volume
     *
     * @param vol The volume the player shall be set to (value between 0 -100)
     * @param pid
     */
    public void setVolume(String vol, String pid) {
        controller.send(controller.command().setVolume(vol, pid));
    }

    /**
     * Increases the HEOS player volume 1 Step
     *
     * @param pid
     */
    public void increaseVolume(String pid) {
        controller.send(controller.command().volumeUp(pid));
    }

    /**
     * Decreases the HEOS player volume 1 Step
     *
     * @param pid
     */
    public void decreaseVolume(String pid) {
        controller.send(controller.command().volumeDown(pid));
    }

    /**
     * Toggles mute state of the HEOS group
     *
     * @param gid The GID of the group
     */
    public void muteGroup(String gid) {
        controller.send(controller.command().setMuteToggle(gid));
    }

    /**
     * Mutes the HEOS group
     *
     * @param gid The GID of the group
     */
    public void muteGroupON(String gid) {
        controller.send(controller.command().setGroupMuteOn(gid));
    }

    /**
     * Un-mutes the HEOS group
     *
     * @param gid The GID of the group
     */
    public void muteGroupOFF(String gid) {
        controller.send(controller.command().setGroupMuteOff(gid));
    }

    /**
     * Set the volume of the group to a specific level
     *
     * @param vol The volume the group shall be set to (value between 0-100)
     * @param gid The GID of the group
     */
    public void volumeGroup(String vol, String gid) {
        controller.send(controller.command().setGroupVolume(vol, gid));
    }

    /**
     * Increases the HEOS group volume 1 Step
     *
     * @param pid
     */
    public void increaseGroupVolume(String gid) {
        controller.send(controller.command().setGroupVolumeUp(gid));
    }

    /**
     * Decreases the HEOS group volume 1 Step
     *
     * @param pid
     */
    public void decreaseGroupVolume(String gid) {
        controller.send(controller.command().setGroupVolumeDown(gid));
    }

    /**
     * Un-Group the HEOS group to single player
     *
     * @param gid The GID of the group
     */
    public void ungroupGroup(String gid) {
        String[] pid = new String[] { gid };
        controller.send(controller.command().setGroup(pid));
    }

    /**
     * Builds a group from single players
     *
     * @param pids The single pid of the player which shall be grouped
     */
    public void groupPlayer(String[] pids) {
        controller.send(controller.command().setGroup(pids));
    }

    /**
     * Browses through a HEOS source. Currently no response
     *
     * @param sid The source sid which shall be browsed
     */
    public void browseSource(String sid) {
        controller.send(controller.command().browseSource(sid));
    }

    /**
     * Adds a media container to the queue and plays the media directly
     * Information of the sid and cid has to be obtained via the browse function
     *
     * @param pid The player ID where the media object shall be played
     * @param sid The source ID where the media is located
     * @param cid The container ID of the media
     */
    public void addContainerToQueuePlayNow(String pid, String sid, String cid) {
        controller.send(controller.command().addContainerToQueuePlayNow(pid, sid, cid));
    }

    /**
     * Sets the connection parameter if the HOES system and connects to the system
     *
     * @param ip The IP address of the HEOS player which is used as bridge
     * @param port The port the system shall establish the connection
     */
    public void setHeosConnection(String ip, int port) {
        controller.setConnectionIP(ip);
        controller.setConnectionPort(port);
        controller.establishConnection(false);
    }

    /**
     * Reboot the bridge to which the connection is established
     */
    public void reboot() {
        controller.sendWithoutResponse(controller.command().rebootSystem());
    }

    /**
     * Login in via the bridge to the HEOS account
     *
     * @param name The username
     * @param password The password of the user
     */
    public void logIn(String name, String password) {
        controller.command().setUsernamePwassword(name, password);
        controller.send(controller.command().signIn(name, password));
    }

    /**
     * Plays a specific station on the HEOS player
     *
     * @param pid The player ID
     * @param sid The source ID where the media is located
     * @param cid The container ID of the media
     * @param mid The media ID of the media
     * @param name Station name returned by 'browse' command.
     */
    public void playStation(String pid, String sid, String cid, String mid, String name) {
        controller.send(controller.command().playStation(pid, sid, cid, mid, name));
    }

    /**
     * Plays a specified local input source on the player.
     * Input name as per specified in HEOS CLI Protocol
     *
     * @param pid
     * @param input
     */
    public void playInputSource(String pid, String input) {
        controller.send(controller.command().playInputSource(pid, pid, input));
    }

    /**
     * Plays a specified input source from another player on the selected player.
     * Input name as per specified in HEOS CLI Protocol
     *
     * @param des_pid the PID where the source shall be played
     * @param source_pid the PID where the source is located.
     * @param input the input name
     */
    public void playInputSource(String des_pid, String source_pid, String input) {
        controller.send(controller.command().playInputSource(des_pid, source_pid, input));
    }

    /**
     * Plays a file from a URL
     *
     * @param pid the PID where the file shall be played
     * @param url the complete URL the file is located
     */
    public void playURL(String pid, URL url) {
        controller.send(controller.command().playURL(pid, url.toString()));
    }

    /**
     * Gets the information like mid, sid and so on of the
     * currently playing media. Response is handled via the
     * HeosEventController
     *
     * @param pid The player ID the media is playing on
     */
    public void getPlayingMediaInfo(String pid) {
        controller.send(controller.command().getNowPlayingMedia(pid));
    }

    /**
     * Deletes a media from the queue
     *
     * @param pid The player ID the media is playing on
     * @param qid The queue ID of the media. (starts by 1)
     */
    public void deleteMediaFromQueue(String pid, String qid) {
        controller.send(controller.command().deleteQueueItem(pid, qid));
    }

    /**
     * Plays a specific media file from te queue
     *
     * @param pid The player ID the media shall be played on
     * @param qid The queue ID of the media. (starts by 1)
     */
    public void playMediafromQueue(String pid, String qid) {
        controller.send(controller.command().playQueueItem(pid, qid));
    }

    /**
     *
     * @param playerID
     */
    public void setActivePlayer(String playerID) {
        controller.command().setPlayerID(playerID);
    }

    /**
     * Sends a RAW command to the HESO bridge. The command has to be
     * in accordance with the HEOS CLI specification
     *
     * @param command to send
     */

    public void sendRawCommand(String command) {
        controller.send(command);
    }

    /**
     * Register an {@linkHeosEventListener} to get notification of system events
     *
     * @param listener The HeosEventListener
     */
    public void registerforChangeEvents(HeosEventListener listener) {
        event.addListener(listener);
    }

    /**
     * Unregister an {@linkHeosEventListener} to get notification of system events
     *
     * @param listener The HeosEventListener
     */
    public void unregisterforChangeEvents(HeosEventListener listener) {
        event.removeListener(listener);
    }
}
