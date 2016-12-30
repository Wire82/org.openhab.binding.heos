package org.openhab.binding.heos.internal.discovery;

import static org.openhab.binding.heos.HeosBindingConstants.*;

import java.util.Collections;
import java.util.HashMap;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.heos.HeosBindingConstants;
import org.openhab.binding.heos.handler.HeosBridgeHandler;
import org.openhab.binding.heos.resources.HeosPlayer;

public class HeosPlayerDiscovery extends AbstractDiscoveryService {

    private HeosBridgeHandler bridge;

    public HeosPlayerDiscovery(HeosBridgeHandler bridge) throws IllegalArgumentException {
        super(Collections.singleton(HeosBindingConstants.THING_TYPE_PLAYER), 20);
        this.bridge = bridge;

    }

    @Override
    protected void startScan() {

        System.out.println("Start Scan for Player");
        HashMap<String, HeosPlayer> playerMap = new HashMap<>();
        playerMap = bridge.getNewPlayer();
        System.out.println("Found: " + playerMap.size() + " new Player");
        createPlayer(playerMap);

    }

    @Override
    protected void startBackgroundDiscovery() {

        System.out.println("start background discover");

    }

    private void createPlayer(HashMap<String, HeosPlayer> playerMap) {

        ThingUID bridgeUID = bridge.getThing().getUID();

        for (String playerPID : playerMap.keySet()) {
            HeosPlayer player = playerMap.get(playerPID);
            ThingUID uid = new ThingUID(THING_TYPE_PLAYER, playerMap.get(playerPID).getPid());
            HashMap<String, Object> properties = new HashMap<String, Object>();
            properties.put(NAME, player.getName());
            properties.put(PID, player.getPid());
            properties.put(PLAYER_TYPE, player.getModel());
            properties.put(HOST, player.getIp());
            DiscoveryResult result = DiscoveryResultBuilder.create(uid).withLabel(player.getName())
                    .withProperties(properties).withBridge(bridgeUID).build();
            thingDiscovered(result);
        }

    }

    // Debug class

    public void debugCreatPlayer() {
        System.out.println("Debug: Creating Player");
        ThingUID uid = new ThingUID("heos", "Player", "test");
        HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put(PID, "2514623");
        properties.put(PLAYER_TYPE, "Heos 7");
        DiscoveryResult result = DiscoveryResultBuilder.create(uid).withLabel("Player").withProperties(properties)
                .build();
        thingDiscovered(result);

    }

}
