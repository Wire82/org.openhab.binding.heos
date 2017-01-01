/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.heos;

import java.util.Collections;
import java.util.Set;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * The {@link HeosBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Johannes Einig - Initial contribution
 */
public class HeosBindingConstants {

    public static final String BINDING_ID = "heos";

    // List of all Bridge Type UIDs

    public final static ThingTypeUID THING_TYPE_BRIDGE = new ThingTypeUID(BINDING_ID, "Bridge");
    public final static ThingTypeUID THING_TYPE_PLAYER = new ThingTypeUID(BINDING_ID, "Player");
    public final static ThingTypeUID THING_TYPE_GROUP = new ThingTypeUID(BINDING_ID, "Group");

    // List of all Channel ids
    public final static String CH_ID_CONTROL = "Control";
    public final static String CH_ID_VOLUME = "Volume";
    public final static String CH_ID_MUTE = "Mute";
    public final static String CH_ID_UNGROUP = "Ungroup";

    public final static String HOST = "ipAddress";
    public final static String PLAYER_TYPE = "model";
    public final static String NAME = "name";
    public final static String USER_NAME = "userName";
    public final static String PASSWORD = "password";
    public final static String PID = "pid";

    public final static String STATE = "state";
    public final static String PLAY = "play";
    public final static String PAUSE = "pause";
    public final static String STOP = "stop";
    public final static String ON = "on";
    public final static String OFF = "off";
    public final static String MUTE = "mute";
    public final static String VOLUME = "volume";

    public static Set<ThingTypeUID> supportedThingTypes() {

        SetView<ThingTypeUID> supportedThings = Sets.union(Collections.singleton(THING_TYPE_PLAYER),
                Collections.singleton(THING_TYPE_BRIDGE));
        // add Collections.singletons
        // supportedThings.add(THING_TYPE_BRIDGE);
        // supportedThings.add(THING_TYPE_GROUP);
        // supportedThings.add(THING_TYPE_PLAYER);

        return supportedThings;
    }

}
