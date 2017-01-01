/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.heos.internal;

import static org.openhab.binding.heos.HeosBindingConstants.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.openhab.binding.heos.HeosBindingConstants;
import org.openhab.binding.heos.api.HeosAPI;
import org.openhab.binding.heos.api.HeosSystem;
import org.openhab.binding.heos.handler.HeosBridgeHandler;
import org.openhab.binding.heos.handler.HeosGroupHandler;
import org.openhab.binding.heos.handler.HeosPlayerHandler;
import org.openhab.binding.heos.internal.discovery.HeosPlayerDiscovery;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link HeosHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Johannes Einig - Initial contribution
 */
public class HeosHandlerFactory extends BaseThingHandlerFactory {

    private Map<ThingUID, ServiceRegistration<?>> discoveryServiceRegs = new HashMap<>();
    private Logger logger = LoggerFactory.getLogger(HeosHandlerFactory.class);
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
            HeosBridgeHandler bridgeHandler = new HeosBridgeHandler((Bridge) thing, heos, api);
            HeosPlayerDiscovery playerDiscovery = new HeosPlayerDiscovery(bridgeHandler);
            discoveryServiceRegs.put(bridgeHandler.getThing().getUID(),
                    bundleContext.registerService(DiscoveryService.class.getName(), playerDiscovery, null));
            logger.info("Register discovery service for HEOS player and HEOS groups by bridge '{}'",
                    bridgeHandler.getThing().getUID().getId());
            return bridgeHandler;
        }
        if (thingTypeUID.equals(THING_TYPE_PLAYER)) {
            return new HeosPlayerHandler(thing, heos, api);
        }
        if (thingTypeUID.equals(THING_TYPE_GROUP)) {
            return new HeosGroupHandler(thing, heos, api);
        }

        return null;
    }

    @Override

    public void unregisterHandler(Thing thing) {

        if (thing.getThingTypeUID().equals(THING_TYPE_BRIDGE)) {
            ServiceRegistration<?> serviceRegistration = this.discoveryServiceRegs.get(thing.getUID());
            if (serviceRegistration != null) {
                serviceRegistration.unregister();
                discoveryServiceRegs.remove(thing.getUID());
                logger.info("Unregister discovery service for HEOS player and HEOS groups by bridge '{}'",
                        thing.getUID().getId());
            }
        }

    }

    @Override
    protected synchronized void removeHandler(ThingHandler thingHandler) {
        // Debug
        System.out.println("Remove Handler");

    }

    @Override
    public void removeThing(ThingUID thingUID) {
        // Debug

    }
}
