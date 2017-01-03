package org.openhab.binding.heos.handler;

import static org.openhab.binding.heos.HeosBindingConstants.*;

import java.util.HashMap;

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
    private HashMap<String, HeosGroup> groupMap;
    private HeosGroup group;
    private Logger logger = LoggerFactory.getLogger(HeosGroupHandler.class);

    public HeosGroupHandler(Thing thing, HeosSystem heos, HeosAPI api) {
        super(thing);
        this.heos = heos;
        this.api = api;
        gid = thing.getConfiguration().get(GID).toString();

    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

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

            }
        } else if (channelUID.getId().equals(CH_ID_VOLUME)) {

            if (command.toString().equals("REFRESH")) {
                System.out.println("Level: " + group.getLevel());
                api.volumeGroup(group.getLevel(), gid);
            } else {
                api.volumeGroup(command.toString(), gid);
            }

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

        Runnable init = new Runnable() {

            @Override
            public void run() {
                initValues();

            }

        };

        init.run();

        api.registerforChangeEvents(this);

        updateStatus(ThingStatus.ONLINE);
        super.initialize();

    }

    @Override
    public void dispose() {
        api.unregisterforChangeEvents(this);

    }

    private void initValues() {

        groupMap = heos.getGroups();
        this.group = new HeosGroup();
        // Debug
        if (groupMap.containsKey(gid)) {
            group = groupMap.get(gid);
        } else {
            System.out.println("no GID Group");

        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        updateState(CH_ID_UNGROUP, OnOffType.OFF);
        updateState(CH_ID_VOLUME, PercentType.valueOf(group.getLevel()));

        if (group.getMute().equals(ON)) {
            updateState(CH_ID_MUTE, OnOffType.ON);
        } else {
            updateState(CH_ID_MUTE, OnOffType.OFF);
        }

        if (group.getState().equals(PLAY)) {
            updateState(CH_ID_CONTROL, PlayPauseType.PLAY);
        }
        if (group.getState().equals(PAUSE) || group.getState().equals(STOP)) {
            updateState(CH_ID_CONTROL, PlayPauseType.PAUSE);
        }
        updateState(CH_ID_SONG, StringType.valueOf(group.getSong()));
        updateState(CH_ID_ARTIST, StringType.valueOf(group.getArtist()));
        updateState(CH_ID_ALBUM, StringType.valueOf(group.getAlbum()));

    }

    @Override
    public void playerStateChangeEvent(String pid, String event, String command) {
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
                        updateState(CH_ID_MUTE, OnOffType.ON);
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

                }

            }
        }

    }

    @Override
    public void bridgeChangeEvent(String event, String command) {
        // TODO Auto-generated method stub

    }

}
