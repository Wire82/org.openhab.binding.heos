/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.heos.internal;

import static org.openhab.binding.heos.HeosBindingConstants.*;

import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.type.ChannelTypeUID;
import org.openhab.binding.heos.handler.HeosBridgeHandler;
import org.openhab.binding.heos.internal.api.HeosFacade;
import org.openhab.binding.heos.internal.handler.HeosChannelHandler;
import org.openhab.binding.heos.internal.handler.HeosChannelHandlerBuildGroup;
import org.openhab.binding.heos.internal.handler.HeosChannelHandlerControl;
import org.openhab.binding.heos.internal.handler.HeosChannelHandlerDynGroupHandling;
import org.openhab.binding.heos.internal.handler.HeosChannelHandlerFavoriteSelect;
import org.openhab.binding.heos.internal.handler.HeosChannelHandlerGrouping;
import org.openhab.binding.heos.internal.handler.HeosChannelHandlerInputs;
import org.openhab.binding.heos.internal.handler.HeosChannelHandlerMute;
import org.openhab.binding.heos.internal.handler.HeosChannelHandlerPlayURL;
import org.openhab.binding.heos.internal.handler.HeosChannelHandlerPlayerSelect;
import org.openhab.binding.heos.internal.handler.HeosChannelHandlerPlaylist;
import org.openhab.binding.heos.internal.handler.HeosChannelHandlerRawCommand;
import org.openhab.binding.heos.internal.handler.HeosChannelHandlerReboot;
import org.openhab.binding.heos.internal.handler.HeosChannelHandlerVolume;

/**
 * @author Johannes Einig - initial contributor
 *
 */

public class HeosChannelHandlerFactory {

    private HeosBridgeHandler bridge;
    private HeosFacade api;
    //Save the created channel handlers in this HashMap, so that you have one channel handler object per handler object
    //this will enable you to save state information within channel handlers
    //remind to cleanup the cache on handlers disposal
    private Map<BaseThingHandler, Map<ChannelUID, HeosBaseChannelHandler>> channelHandlerCache = new HashMap<>();

    public HeosChannelHandlerFactory(HeosBridgeHandler bridge, HeosFacade api) {
        this.bridge = bridge;
        this.api = api;
    }

    public HeosChannelHandler getChannelHandler(BaseThingHandler handler, ChannelUID channelUID) {
        ChannelTypeUID channelTypeUID;
        Channel channel = bridge.getThing().getChannel(channelUID.getId());
        if (channel == null) {
            channelTypeUID = null;
        } else {
            channelTypeUID = channel.getChannelTypeUID();
        }

        Map<ChannelUID, HeosBaseChannelHandler> handlerChannelHandlers = channelHandlerCache.get(handler);

        if(handlerChannelHandlers == null) {
            handlerChannelHandlers = new HashMap();
            handlerChannelHandlers.set(handler, handlerChannelHandlers);
        }

        channelHandler = handlerChannelHandlers.get(channelUID);

        if(channelHandler == null) {
            if (channelUID.getId().equals(CH_ID_CONTROL)) {
                channelHandler = HeosChannelHandlerControl(bridge, api, channelUID);
            }
            if (channelUID.getId().equals(CH_ID_VOLUME)) {
                channelHandler = HeosChannelHandlerVolume(bridge, api, channelUID);
            }
            if (channelUID.getId().equals(CH_ID_MUTE)) {
                channelHandler = HeosChannelHandlerMute(bridge, api, channelUID);
            }
            if (channelUID.getId().equals(CH_ID_PLAY_URL)) {
                channelHandler = HeosChannelHandlerPlayURL(bridge, api, channelUID);
            }
            if (channelUID.getId().equals(CH_ID_INPUTS)) {
                channelHandler = HeosChannelHandlerInputs(bridge, api, channelUID);
            }
            if (channelUID.getId().equals(CH_ID_UNGROUP)) {
                channelHandler = HeosChannelHandlerGrouping(bridge, api, channelUID);
            }
            if (channelUID.getId().equals(CH_ID_RAW_COMMAND)) {
                channelHandler = HeosChannelHandlerRawCommand(bridge, api, channelUID);
            }
            if (channelUID.getId().equals(CH_ID_REBOOT)) {
                channelHandler = HeosChannelHandlerReboot(bridge, api, channelUID);
            }
            if (channelUID.getId().equals(CH_ID_DYNGROUPSHAND)) {
                channelHandler = HeosChannelHandlerDynGroupHandling(bridge, api, channelUID);
            }
            if (channelUID.getId().equals(CH_ID_BUILDGROUP)) {
                channelHandler = HeosChannelHandlerBuildGroup(bridge, api, channelUID);
            }
            if (channelUID.getId().equals(CH_ID_PLAYLISTS)) {
                channelHandler = HeosChannelHandlerPlaylist(bridge, api, channelUID);
            }            
            if (channelTypeUID != null) {
                if (channelTypeUID.equals(CH_TYPE_FAVORIT)) {
                    channelHandler = HeosChannelHandlerFavoriteSelect(bridge, api, channelUID);
                }
                if (channelTypeUID.equals(CH_TYPE_PLAYER)) {
                    channelHandler = HeosChannelHandlerPlayerSelect(bridge, api, channelUID);
                }
            }

            handlerChannelHandlers.set(channelUID, channelHandler);
        }

        return channelHandler;
    }
}
