package org.openhab.binding.heos.handler;

import static org.openhab.binding.heos.HeosBindingConstants.*;

import java.util.HashMap;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.library.types.PlayPauseType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
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

        init.run();

        api.registerforChangeEvents(this);

        updateStatus(ThingStatus.ONLINE);

    }

    @Override
    public void dispose() {
        api.unregisterforChangeEvents(this);
        updateStatus(ThingStatus.OFFLINE);

    }

    private void initValues() {

        playerMap = heos.getPlayerMap();
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
