package org.openhab.binding.heos.internal.discovery;

import static org.openhab.binding.heos.HeosBindingConstants.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.config.discovery.UpnpDiscoveryParticipant;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.jupnp.model.meta.DeviceDetails;
import org.jupnp.model.meta.ModelDetails;
import org.jupnp.model.meta.RemoteDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeosDiscoveryParticipant implements UpnpDiscoveryParticipant {

    private Logger logger = LoggerFactory.getLogger(HeosDiscoveryParticipant.class);

    @Override
    public Set<ThingTypeUID> getSupportedThingTypeUIDs() {

        return Collections.singleton(THING_TYPE_BRIDGE);
    }

    @Override
    public DiscoveryResult createResult(RemoteDevice device) {

        ThingUID uid = getThingUID(device);
        if (uid != null) {

            Map<String, Object> properties = new HashMap<>(2);
            properties.put(HOST, device.getIdentity().getDescriptorURL().getHost());
            properties.put(NAME, device.getDetails().getModelDetails().getModelName());
            properties.put(HEART_BEAT, HEART_BEAT_DELAY);
            DiscoveryResult result = DiscoveryResultBuilder.create(uid).withProperties(properties)
                    .withLabel(device.getDetails().getFriendlyName()).withRepresentationProperty(PLAYER_TYPE).build();
            // Debug
            // System.out.println(device.getDetails().getFriendlyName());
            // System.out.println(uid.toString());
            // System.out.println(device.getDetails().getManufacturerDetails().getManufacturer());
            // System.out.println(device.getDetails().getModelDetails().getModelName());
            // System.out.println(device.getIdentity().getDescriptorURL().getHost().toString());
            // System.out.println(device.getIdentity().getInterfaceMacAddress().toString());
            // System.out.println(device.getIdentity().getUdn().getIdentifierString());
            // System.out.println(device.getType().getType() + "\n");

            logger.info("Found HEOS device with UID: {}", uid.getAsString());
            return result;
        }

        return null;
    }

    @Override
    public ThingUID getThingUID(RemoteDevice device) {
        DeviceDetails details = device.getDetails();
        if (details != null) {
            ModelDetails modelDetails = details.getModelDetails();
            if (modelDetails != null) {
                String modelName = modelDetails.getModelName();
                if (modelName != null) {
                    if (modelName.startsWith("HEOS")) {
                        if (device.getType().getType().startsWith("ACT")) {
                            return new ThingUID(THING_TYPE_BRIDGE, device.getIdentity().getUdn().getIdentifierString());

                        }
                    }
                }
            }

        }

        return null;
    }

}
