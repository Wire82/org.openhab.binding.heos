/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.heos.handler;

import static org.openhab.binding.heos.HeosBindingConstants.*;

import java.util.Collection;
import java.util.HashMap;

import org.eclipse.smarthome.config.discovery.DiscoveryListener;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
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

    private HeosPlayerDiscovery playerDiscovery;
    private HeosSystem heos;
    private HeosAPI api;
    private boolean bridgeIsConnected = false;

    private Logger logger = LoggerFactory.getLogger(HeosBridgeHandler.class);

    public HeosBridgeHandler(Bridge thing, HeosSystem heos, HeosAPI api) {
        super(thing);
        this.heos = heos;
        this.api = api;

    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

    }

    @Override
    public void initialize() {

        logger.debug("Initit Brige '{}' with IP '{}'", thing.getConfiguration().get(NAME),
                thing.getConfiguration().get(HOST));

        heos.setConnectionIP(thing.getConfiguration().get(HOST).toString());
        heos.setConnectionPort(1255);
        bridgeIsConnected = heos.establishConnection();
        while (!bridgeIsConnected) {
            heos.closeConnection();
            bridgeIsConnected = heos.establishConnection();
        }

        Runnable eventListener = new Runnable() {

            @Override
            public void run() {

                heos.getPlayer();
                heos.getGroups();
                heos.startEventListener();
            }
        };

        eventListener.run();
        api.registerforChangeEvents(this);
        updateStatus(ThingStatus.ONLINE);

    }

    @Override
    public void dispose() {
        logger.debug("Dispose Brige '{}'", thing.getConfiguration().get(NAME));
        heos.closeConnection();

    }

    @Override
    public void childHandlerInitialized(ThingHandler childHandler, Thing childThing) {
        handlerList.put(childThing.getUID(), childHandler);
        // Debug
        System.out.println("Init ChildHandler");

    }

    @Override
    public void childHandlerDisposed(ThingHandler childHandler, Thing childThing) {
        handlerList.remove(childThing.getUID(), childHandler);
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
            handlerList.get(thingUID).handleRemoval();
        }

    }

    @Override
    public Collection<ThingUID> removeOlderResults(DiscoveryService source, long timestamp,
            Collection<ThingTypeUID> thingTypeUIDs) {
        // TODO Auto-generated method stub
        return null;
    }

}
