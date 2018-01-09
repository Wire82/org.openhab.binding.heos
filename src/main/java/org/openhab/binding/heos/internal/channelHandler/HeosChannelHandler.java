/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openhab.binding.heos.internal.channelHandler;

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
     * wants to handle the command to decide which subclass has to be used
     *
     * @param command the command to handle
     * @param id of the group or player
     * @param handler The class which wants to handle the command
     */

    public void handleCommand(Command command, String id, Object handler) {
        this.id = id;
        this.handler = handler;
        if (handler.getClass() == HeosPlayerHandler.class) {
            handleCommandPlayer(command);
        } else if (handler.getClass() == HeosGroupHandler.class) {
            handleCommandGroup(command);
        } else if (handler.getClass() == HeosBridgeHandler.class) {
            handleCommandBridge(command);
        }
    }

    /**
     * Handles the command for HEOS player
     *
     * @param command the command to handle
     */

    protected abstract void handleCommandPlayer(Command command);

    /**
     * Handles the command for HEOS groups
     *
     * @param command the command to handle
     */

    protected abstract void handleCommandGroup(Command command);

    /**
     * Handles the command for the HEOS bridge
     *
     * @param command the command to handle
     */

    protected abstract void handleCommandBridge(Command command);
}
