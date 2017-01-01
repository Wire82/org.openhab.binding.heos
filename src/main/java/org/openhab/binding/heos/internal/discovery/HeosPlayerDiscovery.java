package org.openhab.binding.heos.internal.discovery;

import static org.openhab.binding.heos.HeosBindingConstants.*;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.heos.handler.HeosBridgeHandler;
import org.openhab.binding.heos.resources.HeosGroup;
import org.openhab.binding.heos.resources.HeosPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

public class HeosPlayerDiscovery extends AbstractDiscoveryService {

    private final static int SEARCH_TIME = 20;
    private final static int INITIAL_DELAY = 5;
    private final static int SCAN_INTERVAL = 20;

    private Logger logger = LoggerFactory.getLogger(HeosPlayerDiscovery.class);

    private HeosBridgeHandler bridge;

    private PlayerScan scanningRunnable;

    private ScheduledFuture<?> scanningJob;

    public HeosPlayerDiscovery(HeosBridgeHandler bridge) throws IllegalArgumentException {
        super(20);
        // super(Collections.singleton(HeosBindingConstants.THING_TYPE_PLAYER), 20);
        this.bridge = bridge;

        this.scanningRunnable = new PlayerScan();

        // Debug
        // this.startBackgroundDiscovery();

    }

    @Override
    public Set<ThingTypeUID> getSupportedThingTypes() {

        Set<ThingTypeUID> supportedThings = Sets.newHashSet(THING_TYPE_GROUP, THING_TYPE_PLAYER);

        return supportedThings;
    }

    @Override
    protected void startScan() {

        logger.info("Start scan for HEOS Player");

        HashMap<String, HeosPlayer> playerMap = new HashMap<>();
        playerMap = bridge.getNewPlayer();

        if (playerMap == null) {
            // Debug
            System.out.println("Debug: Player Map = Null");

        } else {

            logger.info("Found: {} new Player", playerMap.size());
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

        logger.info("Start scan for HEOS Groups");

        HashMap<String, HeosGroup> groupMap = new HashMap<>();
        groupMap = bridge.getNewGroups();

        if (playerMap == null) {
            // Debug
            System.out.println("Debug: Player Map = Null");

        } else {

            logger.info("Found: {} new Groups", groupMap.size());
            ThingUID bridgeUID = bridge.getThing().getUID();

            for (String groupGID : groupMap.keySet()) {
                HeosGroup group = groupMap.get(groupGID);
                ThingUID uid = new ThingUID(THING_TYPE_GROUP, groupMap.get(groupGID).getGid());
                HashMap<String, Object> properties = new HashMap<String, Object>();
                properties.put(NAME, group.getName());
                properties.put(GID, group.getGid());
                properties.put(LEADER, group.getLeader());
                DiscoveryResult result = DiscoveryResultBuilder.create(uid).withLabel(group.getName())
                        .withProperties(properties).withBridge(bridgeUID).build();
                thingDiscovered(result);

            }

        }

    }

    @Override
    protected void startBackgroundDiscovery() {

        logger.trace("Start HEOS Player background discovery");

        if (scanningJob == null || scanningJob.isCancelled()) {
            this.scanningJob = AbstractDiscoveryService.scheduler.scheduleWithFixedDelay(this.scanningRunnable,
                    INITIAL_DELAY, SCAN_INTERVAL, TimeUnit.SECONDS);
        }
        logger.trace("scanningJob active");

        // Debug
        System.out.println("start background discover");

    }

    @Override
    protected void stopBackgroundDiscovery() {
        logger.debug("Stop HEOS Player background discovery");

        if (scanningJob != null && !scanningJob.isCancelled()) {
            scanningJob.cancel(true);
            scanningJob = null;
        }
    }

    public class PlayerScan implements Runnable {

        @Override
        public void run() {
            startScan();

        }

    }

}
