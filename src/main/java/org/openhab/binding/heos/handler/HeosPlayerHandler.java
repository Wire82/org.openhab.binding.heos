package org.openhab.binding.heos.handler;

import static org.openhab.binding.heos.HeosBindingConstants.*;

import java.util.HashMap;
import java.util.List;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.library.types.PlayPauseType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.builder.ChannelBuilder;
import org.eclipse.smarthome.core.thing.binding.builder.ThingBuilder;
import org.eclipse.smarthome.core.thing.type.ChannelTypeUID;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.heos.api.HeosAPI;
import org.openhab.binding.heos.api.HeosSystem;
import org.openhab.binding.heos.resources.HeosEventListener;
import org.openhab.binding.heos.resources.HeosPlayer;

public class HeosPlayerHandler extends BaseThingHandler implements HeosEventListener {

    private HeosAPI api;
    private HeosSystem heos;
    private String pid;
    private HashMap<String, HeosPlayer> playerMap;
    private HeosPlayer player;

    public HeosPlayerHandler(Thing thing, HeosSystem heos, HeosAPI api) {
        super(thing);
        this.heos = heos;
        this.api = api;
        pid = thing.getConfiguration().get(PID).toString();

    }

    // Deug...

    protected synchronized void thingStructureChanged() {

        ChannelTypeUID type = new ChannelTypeUID("heos", "mute");

        List<Channel> list = thing.getChannels();
        System.out.println("List size: " + list.size());
        for (int i = 0; i < list.size(); i++) {
            Channel ch = list.get(i);

            System.out.println("channel UID: " + ch.getUID());
            System.out.println("channel TypeUID: " + ch.getChannelTypeUID());
            if (ch.getChannelTypeUID().equals("heos:mute")) {
                // type = ch.getChannelTypeUID();
            }

        }

        ThingBuilder thingBuilder = editThing();

        Channel channel = ChannelBuilder.create(new ChannelUID(this.getThing().getUID(), "String"), "Switch")
                .withType(type).build();
        thingBuilder.withChannel(channel);
        updateThing(thingBuilder.build());

        list = thing.getChannels();
        System.out.println("List size: " + list.size());
        for (int i = 0; i < list.size(); i++) {
            Channel ch = list.get(i);
            System.out.println("channel UID: " + ch.getUID());
            System.out.println("channel TypeUID: " + ch.getChannelTypeUID());
            System.out.println("Channel is linked: " + ch.getProperties().toString());
        }

    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // Debug
        // System.out.println(channelUID.getId());
        if (channelUID.getId().equals(CH_ID_CONTROL)) {

            String com = command.toString();

            switch (com) {

                case "PLAY":
                    api.play(pid);
                    break;
                case "PAUSE":
                    api.pause(pid);
                    break;
                case "NEXT":
                    api.next(pid);
                    break;
                case "PREVIOUS":
                    api.prevoious(pid);
                    break;

            }
        } else if (channelUID.getId().equals(CH_ID_VOLUME)) {

            if (command.toString().equals("REFRESH")) {
                api.volume(player.getLevel(), pid);
            } else {
                api.volume(command.toString(), pid);
            }

        } else if (channelUID.getId().equals("Mute")) {

            if (command.toString().equals("ON")) {
                api.muteON(pid);
            } else {
                api.muteOFF(pid);
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

        api.registerforChangeEvents(this);
        init.run();
        addFavorits();
        updateStatus(ThingStatus.ONLINE);

    }

    @Override
    public void dispose() {
        api.unregisterforChangeEvents(this);
        System.out.println("unregister for changes");

    }

    private void initValues() {

        playerMap = heos.getPlayer();
        this.player = new HeosPlayer();
        // Debug
        if (playerMap.containsKey(pid)) {
            player = playerMap.get(pid);
        } else {
            System.out.println("no PID Player");

        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        updateState(CH_ID_VOLUME, PercentType.valueOf(player.getLevel()));

        if (player.getMute().equals(ON)) {
            updateState(CH_ID_MUTE, OnOffType.ON);
        } else {
            updateState(CH_ID_MUTE, OnOffType.OFF);
        }

        if (player.getState().equals(PLAY)) {
            updateState(CH_ID_CONTROL, PlayPauseType.PLAY);
        }
        if (player.getState().equals(PAUSE) || player.getState().equals(STOP)) {
            updateState(CH_ID_CONTROL, PlayPauseType.PAUSE);
        }
        updateState(CH_ID_SONG, StringType.valueOf(player.getSong()));
        updateState(CH_ID_ARTIST, StringType.valueOf(player.getArtist()));
        updateState(CH_ID_ALBUM, StringType.valueOf(player.getAlbum()));

    }

    @Override
    public void playerStateChangeEvent(String pid, String event, String command) {
        if (pid.equals(this.pid)) {
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
                if (command != null) {
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

    }

    @Override
    public void playerMediaChangeEvent(String pid, HashMap<String, String> info) {

        if (pid.equals(this.pid)) {
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

    public void addFavorits() {
        // api.browseSource("1028");

    }

}
