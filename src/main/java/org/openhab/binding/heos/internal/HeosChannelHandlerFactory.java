/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.heos.internal;

import static org.openhab.binding.heos.HeosBindingConstants.*;

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.openhab.binding.heos.handler.HeosBridgeHandler;
import org.openhab.binding.heos.internal.api.HeosFacade;
import org.openhab.binding.heos.internal.channelHandler.HeosChannelHandler;
import org.openhab.binding.heos.internal.channelHandler.HeosChannelHandlerControl;
import org.openhab.binding.heos.internal.channelHandler.HeosChannelHandlerGrouping;
import org.openhab.binding.heos.internal.channelHandler.HeosChannelHandlerInputs;
import org.openhab.binding.heos.internal.channelHandler.HeosChannelHandlerMute;
import org.openhab.binding.heos.internal.channelHandler.HeosChannelHandlerPlayURL;
import org.openhab.binding.heos.internal.channelHandler.HeosChannelHandlerVolume;

/**
 * @author Johannes Einig - initial contributor
 *
 */

public class HeosChannelHandlerFactory {

    private HeosBridgeHandler bridge;
    private HeosFacade api;

    public HeosChannelHandlerFactory(HeosBridgeHandler bridge, HeosFacade api) {
        this.bridge = bridge;
        this.api = api;
    }

    public HeosChannelHandler getChannelHandler(ChannelUID channelUID, String id) {
        if (channelUID.getId().equals(CH_ID_CONTROL)) {
            return new HeosChannelHandlerControl(bridge, api);
        }
        if (channelUID.getId().equals(CH_ID_VOLUME)) {
            return new HeosChannelHandlerVolume(bridge, api);
        }
        if (channelUID.getId().equals(CH_ID_MUTE)) {
            return new HeosChannelHandlerMute(bridge, api);
        }
        if (channelUID.getId().equals(CH_ID_PLAY_URL)) {
            return new HeosChannelHandlerPlayURL(bridge, api);
        }
        if (channelUID.getId().equals(CH_ID_INPUTS)) {
            return new HeosChannelHandlerInputs(bridge, api);
        }
        if (channelUID.getId().equals(CH_ID_UNGROUP)) {
            return new HeosChannelHandlerGrouping(bridge, api);
        }
        return null;
    }

}
