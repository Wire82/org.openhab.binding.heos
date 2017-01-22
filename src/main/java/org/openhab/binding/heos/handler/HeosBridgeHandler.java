/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.heos.handler;

import static org.openhab.binding.heos.HeosBindingConstants.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.smarthome.config.discovery.DiscoveryListener;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.builder.ChannelBuilder;
import org.eclipse.smarthome.core.thing.binding.builder.ThingBuilder;
import org.eclipse.smarthome.core.thing.type.ChannelTypeUID;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.heos.api.HeosAPI;
import org.openhab.binding.heos.api.HeosSystem;
import org.openhab.binding.heos.internal.discovery.HeosPlayerDiscovery;
import org.openhab.binding.heos.resources.HeosEventListener;
import org.openhab.binding.heos.resources.HeosGroup;
import org.openhab.binding.heos.resources.HeosPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link HeosSystemHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Johannes Einig - Initial contribution
 */
public class HeosBridgeHandler extends BaseBridgeHandler implements HeosEventListener, DiscoveryListener {

    private HashMap<ThingUID, ThingHandler> handlerList = new HashMap<>();
    private InitProcedure init;
    private HeosPlayerDiscovery playerDiscovery;
    private HeosSystem heos;
    private HeosAPI api;
    private boolean bridgeIsConnected = false;
    private HashMap<String, String> playerPID = new HashMap<String, String>();
    private boolean handleGroups = false;
    private boolean logedIn = false;

    private Logger logger = LoggerFactory.getLogger(HeosBridgeHandler.class);

    public HeosBridgeHandler(Bridge thing, HeosSystem heos, HeosAPI api) {
        super(thing);
        this.heos = heos;
        this.api = api;
        this.init = new InitProcedure();

    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        Channel channel = this.thing.getChannel(channelUID.getId());

        if (channel.getChannelTypeUID().toString().equals("heos:player")) {
            if (command.toString().equals("ON")) {
                playerPID.put(channel.getProperties().get(PID), channelUID.getId());
            } else {
                playerPID.remove(channel.getProperties().get(PID));
            }

        }
        if (channel.getChannelTypeUID().toString().equals("heos:favorit")) {
            if (command.toString().equals("ON")) {
                if (!playerPID.isEmpty()) {
                    for (String key : playerPID.keySet()) {
                        String pid = key;
                        String mid = channelUID.getId();
                        api.playStation(pid, FAVORIT_SID, null, mid, null);
                        updateState(channelUID, OnOffType.OFF);
                    }
                }
                playerPID.clear();
            }
        }

        if (channelUID.getId().equals(CH_ID_BUILDGROUP)) {
            if (command.toString().equals("ON")) {
                if (!playerPID.isEmpty()) {
                    String[] player = new String[playerPID.size()];
                    int i = 0;
                    for (String key : playerPID.keySet()) {
                        player[i] = key;
                        ++i;
                    }

                    api.groupPlayer(player);

                    for (String key : playerPID.keySet()) {
                        updateState(playerPID.get(key), OnOffType.OFF);
                    }
                    playerPID.clear();
                    updateState(CH_ID_BUILDGROUP, OnOffType.OFF);
                }

            }
        }
        if (channelUID.getId().equals(CH_ID_DYNGROUPSHAND)) {
            if (command.toString().equals("ON")) {
                handleGroups = true;
            } else {
                handleGroups = false;
            }
        }
        if (channelUID.getId().equals(CH_ID_REBOOT)) {
            if (command.toString().equals("ON")) {
                api.reboot();
                updateState(CH_ID_REBOOT, OnOffType.OFF);
            }
        }
    }

    @Override
    public synchronized void initialize() {

        logger.info("Initit Brige '{}' with IP '{}'", thing.getConfiguration().get(NAME),
                thing.getConfiguration().get(HOST));

        heos.setConnectionIP(thing.getConfiguration().get(HOST).toString());
        heos.setConnectionPort(1255);
        bridgeIsConnected = heos.establishConnection();
        while (!bridgeIsConnected) {
            heos.closeConnection();
            bridgeIsConnected = heos.establishConnection();
        }

        api.registerforChangeEvents(this);
        init.run();

    }

    @Override
    public void dispose() {
        logger.info("Dispose Brige '{}'", thing.getConfiguration().get(NAME));
        heos.closeConnection();

    }

    private void logIn() {
        if (thing.getConfiguration().containsKey(USER_NAME) && thing.getConfiguration().containsKey(PASSWORD)) {

            // Debug
            logger.info("Logging in to HEOS account.");
            String name = this.thing.getConfiguration().get(USER_NAME).toString();
            String password = this.thing.getConfiguration().get(PASSWORD).toString();
            api.logIn(name, password);
            logedIn = true;

        } else {
            logger.error("Can not log in. Username and Password not set");
        }

    }

    @Override
    public void childHandlerInitialized(ThingHandler childHandler, Thing childThing) {
        handlerList.put(childThing.getUID(), childHandler);
        this.addPlayerChannel(childThing);

        // Debug
        // List<Channel> list = thing.getChannels();
        // list = thing.getChannels();
        // System.out.println("List size: " + list.size());
        // for (int i = 0; i < list.size(); i++) {
        // Channel ch = list.get(i);
        // System.out.println("channel UID: " + ch.getUID());
        // System.out.println("channel TypeUID: " + ch.getChannelTypeUID());
        // System.out.println("Channel is linked: " + ch.getProperties().toString());
        // }

        // Debug
        System.out.println("Init ChildHandler");

    }

    @Override
    public void childHandlerDisposed(ThingHandler childHandler, Thing childThing) {
        handlerList.remove(childThing.getUID(), childHandler);
        this.removeChannel(CH_TYPE_PLAYER, childThing.getConfiguration().get(PID).toString());
        // Debug
        System.out.println("Remove ChildHandler");

    }

    public HashMap<String, HeosPlayer> getNewPlayer() {

        return heos.getPlayer();
    }

    public HashMap<String, HeosGroup> getNewGroups() {

        return heos.getGroups();
    }

    public HashMap<String, HeosGroup> getRemovedGroups() {

        return heos.getGroupsRemoved();
    }

    public void setHeosPlayerDiscovery(HeosPlayerDiscovery discover) {
        this.playerDiscovery = discover;
    }

    @Override
    public void playerStateChangeEvent(String pid, String event, String command) {
        // Do nothing

    }

    @Override
    public void playerMediaChangeEvent(String pid, HashMap<String, String> info) {
        // Do nothing

    }

    @Override
    public void bridgeChangeEvent(String event, String command) {
        playerDiscovery.scanForNewPlayers();

    }

    @Override
    public void thingDiscovered(DiscoveryService source, DiscoveryResult result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void thingRemoved(DiscoveryService source, ThingUID thingUID) {
        if (handlerList.get(thingUID) != null) {
            if (handleGroups) {
                handlerList.get(thingUID).handleRemoval();
            } else {
                if (handlerList.get(thingUID).getClass().equals(HeosGroupHandler.class)) {
                    HeosGroupHandler handler = (HeosGroupHandler) handlerList.get(thingUID);
                    handler.setStatusOffline();
                }

            }

        }

    }

    @Override
    public Collection<ThingUID> removeOlderResults(DiscoveryService source, long timestamp,
            Collection<ThingTypeUID> thingTypeUIDs) {
        // TODO Auto-generated method stub
        return null;
    }

    public void addFavorits() {
        if (logedIn) {
            removeChannels(CH_TYPE_FAVORIT);

            List<HashMap<String, String>> favList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> favorits = new HashMap<String, String>(4);
            favList = heos.getFavorits();
            int favCount = favList.size();
            ArrayList<Channel> favoritChannels = new ArrayList<Channel>(favCount);
            // Debug
            System.out.println("Favorite Count " + favCount);
            if (favCount != 0) {
                for (int i = 0; i < favCount; i++) {
                    for (String key : favList.get(i).keySet()) {
                        if (key.equals(MID)) {
                            favorits.put(key, favList.get(i).get(key));
                        }
                        if (key.equals(NAME)) {
                            favorits.put(key, favList.get(i).get(key));
                        }
                        if (key.equals(null)) {

                        }

                    }
                    // Debug
                    System.out.println("Add Channel: " + favorits.get(NAME));

                    favoritChannels.add(createFavoritChannel(favorits));
                }

            }
            addChannel(favoritChannels);

        }

    }

    private void addPlayerChannel(Thing childThing) {

        String playerName = childThing.getConfiguration().get(NAME).toString();
        ChannelUID channelUID = new ChannelUID(this.getThing().getUID(), playerName);
        if (!hasChannel(channelUID)) {
            HashMap<String, String> properties = new HashMap<String, String>(2);
            properties.put(NAME, childThing.getConfiguration().get(NAME).toString());
            properties.put(PID, childThing.getConfiguration().get(PID).toString());

            Channel channel = ChannelBuilder.create(channelUID, "Switch").withLabel(playerName).withType(CH_TYPE_PLAYER)
                    .withProperties(properties).build();

            ArrayList<Channel> newChannelList = new ArrayList<>(1);
            newChannelList.add(channel);
            addChannel(newChannelList);
            // ThingBuilder thingBuilder = editThing();
            // thingBuilder.withChannel(channel);
            // updateThing(thingBuilder.build());
        }

    }

    private void addChannel(List<Channel> newChannelList) {
        List<Channel> existingChannelList = thing.getChannels();
        ArrayList<Channel> mutableChannelList = new ArrayList<Channel>();
        mutableChannelList.addAll(existingChannelList);
        mutableChannelList.addAll(newChannelList);

        ThingBuilder thingBuilder = editThing();
        thingBuilder.withChannels(mutableChannelList);
        updateThing(thingBuilder.build());

    }

    private Channel createFavoritChannel(HashMap<String, String> properties) {

        String favoritName = properties.get(NAME);
        Channel channel = ChannelBuilder.create(new ChannelUID(this.getThing().getUID(), properties.get(MID)), "Switch")
                .withLabel(favoritName).withType(CH_TYPE_FAVORIT).withProperties(properties).build();

        return channel;

    }

    private void removeChannels(ChannelTypeUID channelType) {
        List<Channel> channelList = thing.getChannels();
        ArrayList<Channel> mutableChannelList = new ArrayList<Channel>();
        mutableChannelList.addAll(channelList);
        for (int i = 0; i < mutableChannelList.size(); i++) {
            if (channelList.get(i).getChannelTypeUID().equals(channelType)) {
                mutableChannelList.remove(i);
                i = 0;
            }
        }

        ThingBuilder thingBuilder = editThing();
        thingBuilder.withChannels(mutableChannelList);
        updateThing(thingBuilder.build());
    }

    private void removeChannel(ChannelTypeUID channelType, String name) {
        List<Channel> channelList = thing.getChannels();
        ArrayList<Channel> mutableChannelList = new ArrayList<Channel>();
        mutableChannelList.addAll(channelList);
        for (int i = 0; i < mutableChannelList.size(); i++) {
            if (channelList.get(i).getChannelTypeUID().equals(channelType)) {
                mutableChannelList.remove(i);
            }

        }
        ThingBuilder thingBuilder = editThing();
        thingBuilder.withChannels(mutableChannelList);
        updateThing(thingBuilder.build());
    }

    private boolean hasChannel(ChannelUID channelUID) {
        List<Channel> channelList = thing.getChannels();
        for (int i = 0; i < channelList.size(); i++) {
            if (channelList.get(i).getUID().equals(channelUID)) {
                return true;
            }
        }
        return false;
    }

    public class InitProcedure implements Runnable {

        @Override
        public void run() {
            updateStatus(ThingStatus.OFFLINE);
            heos.getPlayer();
            heos.getGroups();
            logIn();
            addFavorits();
            heos.startEventListener();
            updateStatus(ThingStatus.ONLINE);

        }
    }

}
