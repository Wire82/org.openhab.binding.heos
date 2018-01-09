/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.heos.internal.channelHandler;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.heos.handler.HeosBridgeHandler;
import org.openhab.binding.heos.internal.api.HeosFacade;

/**
 * @author Johannes Einig - initial contributor
 *
 */
public class HeosChannelHandlerMute extends HeosChannelHandler {

    /**
     * @param bridge
     * @param api
     */
    public HeosChannelHandlerMute(HeosBridgeHandler bridge, HeosFacade api) {
        super(bridge, api);
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openhab.binding.heos.handler.factory.HeosChannelHandler#handleCommandPlayer(org.eclipse.smarthome.core.types.
     * Command)
     */
    @Override
    protected void handleCommandPlayer(Command command) {
        if (command.equals(OnOffType.ON)) {
            api.muteON(id);
        } else if (command.equals(OnOffType.OFF)) {
            api.muteOFF(id);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openhab.binding.heos.handler.factory.HeosChannelHandler#handleCommandGroup(org.eclipse.smarthome.core.types.
     * Command)
     */
    @Override
    protected void handleCommandGroup(Command command) {
        if (command.equals(OnOffType.ON)) {
            api.muteGroupON(id);
        } else if (command.equals(OnOffType.OFF)) {
            api.muteGroupOFF(id);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openhab.binding.heos.handler.factory.HeosChannelHandler#handleCommandBridge(org.eclipse.smarthome.core.types.
     * Command)
     */
    @Override
    protected void handleCommandBridge(Command command) {
        // No such channel on bridge
    }

}
