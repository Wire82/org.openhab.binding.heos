/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openhab.binding.heos.internal.handler;

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.heos.handler.HeosBridgeHandler;
import org.openhab.binding.heos.handler.HeosGroupHandler;
import org.openhab.binding.heos.handler.HeosPlayerHandler;
import org.openhab.binding.heos.internal.api.HeosFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Johannes Einig - initial contributor
 *
 */

public abstract class HeosChannelHandler {

    protected Object handler;
    protected HeosBridgeHandler bridge;
    protected HeosFacade api;
    protected String id;
    protected Command command;
    protected ChannelUID channelUID;
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     *
     * @param bridge Requires the HeosBridgeHandler
     * @param api The HeosFacade class
     */
    public HeosChannelHandler(HeosBridgeHandler bridge, HeosFacade api) {
        this.bridge = bridge;
        this.api = api;
    }

    /**
     * Handle a command received from a channel. Requires the class which
     * wants to handle the command to decide which subclass has to be used *
     * 
     * @param command the command to handle
     * @param id of the group or player; Null if bridge
     * @param handler The class which wants to handle the command
     * @param channelUID the channelUID of the handleCommand function
     */

    public void handleCommand(Command command, String id, Object handler, ChannelUID channelUID) {
        this.command = command;
        this.id = id;
        this.handler = handler;
        this.channelUID = channelUID;

        if (handler.getClass() == HeosPlayerHandler.class) {
            handleCommandPlayer();
        } else if (handler.getClass() == HeosGroupHandler.class) {
            handleCommandGroup();
        } else if (handler.getClass() == HeosBridgeHandler.class) {
            handleCommandBridge();
        }
    }

    /**
     * Handles the command for HEOS player
     */

    protected abstract void handleCommandPlayer();

    /**
     * Handles the command for HEOS groups
     */

    protected abstract void handleCommandGroup();

    /**
     * Handles the command for the HEOS bridge
     */

    protected abstract void handleCommandBridge();
}
