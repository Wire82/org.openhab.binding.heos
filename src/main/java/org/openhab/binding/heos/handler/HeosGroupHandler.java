package org.openhab.binding.heos.handler;

import static org.openhab.binding.heos.HeosBindingConstants.*;
import static org.openhab.binding.heos.resources.HeosConstants.GID;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.library.types.PlayPauseType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.heos.api.HeosAPI;
import org.openhab.binding.heos.api.HeosSystem;
import org.openhab.binding.heos.resources.HeosEventListener;
import org.openhab.binding.heos.resources.HeosGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeosGroupHandler extends BaseThingHandler implements HeosEventListener {

    private HeosAPI api;
    private HeosSystem heos;
    private String gid;

    private HeosGroup heosGroup;
    private Logger logger = LoggerFactory.getLogger(HeosGroupHandler.class);

    public HeosGroupHandler(Thing thing, HeosSystem heos, HeosAPI api) {
        super(thing);
        this.heos = heos;
        this.api = api;
        gid = thing.getConfiguration().get(GID).toString();

    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        if (command.toString().equals("REFRESH")) {
            return;
        }

        if (channelUID.getId().equals(CH_ID_CONTROL)) {

            String com = command.toString();

            switch (com) {

                case "PLAY":
                    api.play(gid);
                    break;
                case "PAUSE":
                    api.pause(gid);
                    break;
                case "NEXT":
                    api.next(gid);
                    break;
                case "PREVIOUS":
                    api.prevoious(gid);
                    break;
                case "ON":
                    api.play(gid);
                    break;
                case "OFF":
                    api.pause(gid);
                    break;

            }
        } else if (channelUID.getId().equals(CH_ID_VOLUME)) {

            api.volumeGroup(command.toString(), gid);

        } else if (channelUID.getId().equals(CH_ID_MUTE)) {

            if (command.toString().equals("ON")) {

                api.muteGroupON(gid);
            } else {
                api.muteGroupOFF(gid);
            }
        } else if (channelUID.getId().equals(CH_ID_UNGROUP)) {

            if (command.toString().equals("ON")) {
                api.ungroupGroup(gid);

            }
        }

    }

    @Override
    public void initialize() {

        api.registerforChangeEvents(this);
        ScheduledExecutorService executerPool = Executors.newScheduledThreadPool(1);
        executerPool.schedule(new InitializationRunnable(), 4, TimeUnit.SECONDS);
        updateStatus(ThingStatus.ONLINE);
        updateState(CH_ID_STATUS, StringType.valueOf(ONLINE));
        super.initialize();

    }

    @Override
    public void dispose() {
        api.unregisterforChangeEvents(this);
        super.dispose();

    }

    @Override
    public void playerStateChangeEvent(String pid, String event, String command) {

        if (this.getThing().getStatus().toString().equals(ThingStatus.UNINITIALIZED)) {
            logger.error("Can't Handle Event. Group {} not initialized. Status is: {}", this.getConfig().get(NAME),
                    this.getThing().getStatus().toString());
            return;

        }

        if (pid.equals(this.gid)) {
            if (event.equals(STATE)) {
                switch (command) {

                    case PLAY:
                        updateState(CH_ID_CONTROL, PlayPauseType.PLAY);
                        break;
                    case PAUSE:
                        updateState(CH_ID_CONTROL, PlayPauseType.PAUSE);
                        break;
                    case STOP:
                        updateState(CH_ID_CONTROL, PlayPauseType.PAUSE);
                        break;
                }

            }
            if (event.equals(VOLUME)) {

                updateState(CH_ID_VOLUME, PercentType.valueOf(command));

            }
            if (event.equals(MUTE)) {

                switch (command) {
                    case ON:
                        this.updateState(CH_ID_MUTE, OnOffType.ON);
                        break;
                    case OFF:
                        updateState(CH_ID_MUTE, OnOffType.OFF);
                        break;
                }

            }

        }

    }

    @Override
    public void playerMediaChangeEvent(String pid, HashMap<String, String> info) {
        if (pid.equals(this.gid)) {

            for (String key : info.keySet()) {

                switch (key) {

                    case SONG:
                        updateState(CH_ID_SONG, StringType.valueOf(info.get(key)));
                        break;
                    case ARTIST:
                        updateState(CH_ID_ARTIST, StringType.valueOf(info.get(key)));
                        break;
                    case ALBUM:
                        updateState(CH_ID_ALBUM, StringType.valueOf(info.get(key)));
                        break;
                    case IMAGE_URL:
                        updateState(CH_ID_IMAGE_URL, StringType.valueOf(info.get(key)));
                        break;

                }

            }
        }

    }

    @Override
    public void bridgeChangeEvent(String event, String result, String command) {
        // TODO Auto-generated method stub

    }

    public void setStatusOffline() {
        api.unregisterforChangeEvents(this);
        updateState(CH_ID_STATUS, StringType.valueOf(OFFLINE));
        updateStatus(ThingStatus.OFFLINE);
    }

    public class InitializationRunnable implements Runnable {

        @Override
        public void run() {

            heosGroup = heos.getGroupState(gid);

            HeosBridgeHandler bridge = (HeosBridgeHandler) getBridge().getHandler();

            if (!thing.getConfiguration().get(NAME_HASH).equals(heosGroup.getNameHash())) {
                updateStatus(ThingStatus.OFFLINE);
                setStatusOffline();
                bridge.thingStatusOffline(thing.getUID());
                updateState(CH_ID_STATUS, StringType.valueOf(OFFLINE));
                return;
            }

            // informs the System about the existing group

            bridge.thingStatusOnline(thing.getUID());
            HashMap<String, HeosGroup> usedToFillOldGroupMap = new HashMap<>();
            usedToFillOldGroupMap.put(heosGroup.getNameHash(), heosGroup);
            heos.addHeosGroupToOldGroupMap(usedToFillOldGroupMap);

            updateState(CH_ID_UNGROUP, OnOffType.OFF);
            updateState(CH_ID_VOLUME, PercentType.valueOf(heosGroup.getLevel()));

            if (heosGroup.getMute().equals(ON)) {
                updateState(CH_ID_MUTE, OnOffType.ON);
            } else {
                updateState(CH_ID_MUTE, OnOffType.OFF);
            }

            if (heosGroup.getState().equals(PLAY)) {
                updateState(CH_ID_CONTROL, PlayPauseType.PLAY);
            }
            if (heosGroup.getState().equals(PAUSE) || heosGroup.getState().equals(STOP)) {
                updateState(CH_ID_CONTROL, PlayPauseType.PAUSE);
            }
            updateState(CH_ID_SONG, StringType.valueOf(heosGroup.getSong()));
            updateState(CH_ID_ARTIST, StringType.valueOf(heosGroup.getArtist()));
            updateState(CH_ID_ALBUM, StringType.valueOf(heosGroup.getAlbum()));
            updateState(CH_ID_IMAGE_URL, StringType.valueOf(heosGroup.getImage_url()));
            updateState(CH_ID_STATUS, StringType.valueOf(ONLINE));

        }

    }

}
