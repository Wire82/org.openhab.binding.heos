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

public abstract class BaseHeosChannelHandler {

    protected BaseThingHandler handler;
    protected HeosBridgeHandler bridge;
    protected HeosFacade api;
    protected String id;
    protected ChannelUID channelUID;
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     *
     * @param bridge Requires the HeosBridgeHandler
     * @param api The HeosFacade class
     */
    public HeosBaseChannelHandler(HeosBridgeHandler bridge, HeosFacade api) {
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

    public void handleCommand(Command command, String id, BaseThingHandler handler, ChannelUID channelUID) {
        this.id = id;
        this.handler = handler;
        this.channelUID = channelUID;

        if (handler instanceof HeosPlayerHandler) {
            ((HeosPlayerChannelHandler) this).handleCommandOnPlayer(command);
        } else if (handler instanceof HeosGroupHandler) {
            ((HeosGroupChannelHandler) this).handleCommandOnGroup(command);
        } else if (handler instanceof HeosBridgeHandler) {
            ((HeosBidgeChannelHandler) this).handleCommandOnBridge(command);
        }
    }
}