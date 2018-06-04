/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.heos.internal.discovery;

import static org.openhab.binding.heos.HeosBindingConstants.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.config.discovery.upnp.UpnpDiscoveryParticipant;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.jupnp.model.meta.DeviceDetails;
import org.jupnp.model.meta.ManufacturerDetails;
import org.jupnp.model.meta.ModelDetails;
import org.jupnp.model.meta.RemoteDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link HeosDiscoveryParticipant} discovers the HEOS Player of the
 * network via an UPnP interface.
 *
 * @author Johannes Einig - Initial contribution
 */
@NonNullByDefault
public class HeosDiscoveryParticipant implements UpnpDiscoveryParticipant {

    private Logger logger = LoggerFactory.getLogger(HeosDiscoveryParticipant.class);

    @Override
    public Set<ThingTypeUID> getSupportedThingTypeUIDs() {
        return Collections.singleton(THING_TYPE_BRIDGE);
    }

    @Override
    public @Nullable DiscoveryResult createResult(RemoteDevice device) {
        ThingUID uid = getThingUID(device);
        if (uid != null) {
            Map<String, Object> properties = new HashMap<>(2);
            properties.put(HOST, device.getIdentity().getDescriptorURL().getHost());
            properties.put(NAME, device.getDetails().getModelDetails().getModelName());
            DiscoveryResult result = DiscoveryResultBuilder.create(uid).withProperties(properties)
                    .withLabel(" Bridge - " + device.getDetails().getFriendlyName())
                    .withRepresentationProperty(PLAYER_TYPE).build();
            logger.info("Found HEOS device with UID: {}", uid.getAsString());
            return result;
        }
        return null;
    }

    @Override
    public @Nullable ThingUID getThingUID(RemoteDevice device) {

        // System.out.println("Name: " + device.getDetails().getModelDetails().getModelName());
        // System.out.println("Manufac: " + device.getDetails().getManufacturerDetails().getManufacturer());
        // System.out.println("Type " + device.getType().getType());
        // System.out.println("Friendly Name " + device.getDetails().getFriendlyName());
        // System.out.println("UPC " + device.getDetails().getUpc());

        Optional<RemoteDevice> optDevice = Optional.ofNullable(device);
        String modelName = optDevice.map(RemoteDevice::getDetails).map(DeviceDetails::getModelDetails)
                .map(ModelDetails::getModelName).orElse("UNKNOWN");
        String modelManufacturer = optDevice.map(RemoteDevice::getDetails).map(DeviceDetails::getManufacturerDetails)
                .map(ManufacturerDetails::getManufacturer).orElse("UNKNOWN");

        if (modelManufacturer.equals("Denon")) {
            if (modelName.startsWith("HEOS") || modelName.endsWith("H")) {
                String deviceType = device.getType().getType();
                if (deviceType.startsWith("ACT") || deviceType.startsWith("Aios")) {
                    return new ThingUID(THING_TYPE_BRIDGE,
                            optDevice.get().getIdentity().getUdn().getIdentifierString());
                }
            }
        }
        return null;

        // optDevice.map(RemoteDevice::getDetails).map(DeviceDetails::getModelDetails).map(ModelDetails::getModelName)
        // .ifPresent(modelName -> {
        // optDevice.map(RemoteDevice::getDetails).map(DeviceDetails::getManufacturerDetails)
        // .map(ManufacturerDetails::getManufacturer).ifPresent(modelManufacturer -> {
        // if (modelManufacturer.equals("Denon")) {
        // if (modelName.startsWith("HEOS") || modelName.endsWith("H")) {
        // if (device.getType().getType().startsWith("ACT")) {
        // thingUID = new ThingUID(THING_TYPE_BRIDGE,
        // optDevice.get().getIdentity().getUdn().getIdentifierString());
        // }
        // }
        // }
        // });
        // });
        // return thingUID;

        // DeviceDetails details = device.getDetails();
        // if (details != null) {
        // ModelDetails modelDetails = details.getModelDetails();
        // ManufacturerDetails modelManufacturerDetails = details.getManufacturerDetails();
        // if (modelDetails != null && modelManufacturerDetails != null) {
        // String modelName = modelDetails.getModelName();
        // String modelManufacturer = modelManufacturerDetails.getManufacturer();
        // if (modelName != null && modelManufacturer != null) {
        // if (modelManufacturer.equals("Denon")) {
        // if (modelName.startsWith("HEOS") || modelName.endsWith("H")) {
        // if (device.getType().getType().startsWith("ACT")) {
        // return new ThingUID(THING_TYPE_BRIDGE,
        // device.getIdentity().getUdn().getIdentifierString());
        // }
        // }
        // }
        // }
        // }
        // }
        // return null;
    }
}
