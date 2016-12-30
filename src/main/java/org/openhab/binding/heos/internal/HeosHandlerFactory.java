/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.heos.internal;

import static org.openhab.binding.heos.HeosBindingConstants.*;

import java.util.Set;

import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.openhab.binding.heos.HeosBindingConstants;
import org.openhab.binding.heos.api.HeosAPI;
import org.openhab.binding.heos.api.HeosSystem;
import org.openhab.binding.heos.handler.HeosBridgeHandler;
import org.openhab.binding.heos.handler.HeosPlayerHandler;
import org.openhab.binding.heos.internal.discovery.HeosPlayerDiscovery;

/**
 * The {@link HeosHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Johannes Einig - Initial contribution
 */
public class HeosHandlerFactory extends BaseThingHandlerFactory {

    private HeosSystem heos = new HeosSystem();
    private HeosAPI api = heos.getAPI();

    private final static Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = HeosBindingConstants.supportedThingTypes();

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {

        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(THING_TYPE_BRIDGE)) {
            HeosBridgeHandler systemHandler = new HeosBridgeHandler((Bridge) thing, heos, api);
            HeosPlayerDiscovery playerDiscovery = new HeosPlayerDiscovery(systemHandler);
            // Debug
            // System.out.println("Debug: Register Service");
            bundleContext.registerService(DiscoveryService.class.getName(), playerDiscovery, null);
            return systemHandler;
        }
        if (thingTypeUID.equals(THING_TYPE_PLAYER)) {
            return new HeosPlayerHandler(thing, heos, api);
        }

        return null;
    }

}
