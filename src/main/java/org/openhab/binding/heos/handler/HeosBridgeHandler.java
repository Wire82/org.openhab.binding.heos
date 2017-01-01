/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.heos.handler;

import static org.openhab.binding.heos.HeosBindingConstants.*;

import java.util.HashMap;

import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.heos.api.HeosAPI;
import org.openhab.binding.heos.api.HeosSystem;
import org.openhab.binding.heos.resources.HeosPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link HeosSystemHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Johannes Einig - Initial contribution
 */
public class HeosBridgeHandler extends BaseBridgeHandler {

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

        Runnable eventListener = new Runnable() {

            @Override
            public void run() {
                bridgeIsConnected = heos.establishConnection();
                while (!bridgeIsConnected) {
                    heos.closeConnection();
                    bridgeIsConnected = heos.establishConnection();
                }
                heos.getPlayer();
                heos.getGroups();
            }
        };

        eventListener.run();
        updateStatus(ThingStatus.ONLINE);

    }

    @Override
    public void dispose() {
        logger.debug("Dispose Brige '{}'", thing.getConfiguration().get(NAME));

        heos.closeConnection();

    }

    @Override
    public void childHandlerInitialized(ThingHandler childHandler, Thing childThing) {
        System.out.println("Init ChildHandler");

    }

    @Override
    public void childHandlerDisposed(ThingHandler childHandler, Thing childThing) {
        System.out.println("Remove ChildHandler");

    }

    public HashMap<String, HeosPlayer> getNewPlayer() {

        return heos.getPlayer();
    }

}
