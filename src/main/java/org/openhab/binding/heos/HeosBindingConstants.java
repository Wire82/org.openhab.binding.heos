/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.heos;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.type.ChannelTypeUID;

/**
 * The {@link HeosBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Johannes Einig - Initial contribution
 */

@NonNullByDefault
public class HeosBindingConstants {

    public static final String BINDING_ID = "heos";

    // List of all Bridge Type UIDs

    public static final ThingTypeUID THING_TYPE_BRIDGE = new ThingTypeUID(BINDING_ID, "bridge");
    public static final ThingTypeUID THING_TYPE_PLAYER = new ThingTypeUID(BINDING_ID, "player");
    public static final ThingTypeUID THING_TYPE_GROUP = new ThingTypeUID(BINDING_ID, "group");

    // List off all Channel Types

    public static final ChannelTypeUID CH_TYPE_PLAYER = new ChannelTypeUID(BINDING_ID, "ch_player");
    public static final ChannelTypeUID CH_TYPE_FAVORIT = new ChannelTypeUID(BINDING_ID, "ch_favorit");
    public static final ChannelTypeUID CH_TYPE_GROUP = new ChannelTypeUID(BINDING_ID, "ch_group");

    // List of all Channel IDs
    public static final String CH_ID_CONTROL = "Control";
    public static final String CH_ID_VOLUME = "Volume";
    public static final String CH_ID_MUTE = "Mute";
    public static final String CH_ID_UNGROUP = "Ungroup";
    public static final String CH_ID_SONG = "Title";
    public static final String CH_ID_ARTIST = "Interpret";
    public static final String CH_ID_ALBUM = "Album";
    public static final String CH_ID_PLAYER = "Player";
    public static final String CH_ID_BUILDGROUP = "BuildGroup";
    public static final String CH_ID_DYNGROUPSHAND = "DynamicGroupHandling";
    public static final String CH_ID_REBOOT = "Reboot";
    public static final String CH_ID_IMAGE_URL = "ImageUrl";
    public static final String CH_ID_PLAYLISTS = "Playlists";
    public static final String CH_ID_INPUTS = "Inputs";
    public static final String CH_ID_STATUS = "OnlineStatus";
    public static final String CH_ID_CUR_POS = "CurrentPosition";
    public static final String CH_ID_DURATION = "Duration";
    public static final String CH_ID_STATION = "Station";
    public static final String CH_ID_RAW_COMMAND = "RawCommand";
    public static final String CH_ID_TYPE = "Type";
    public static final String CH_ID_PLAY_URL = "PlayUrl";

    public static final String HOST = "ipAddress";
    public static final String PLAYER_TYPE = "model";
    public static final String NAME = "name";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String HEARTBEAT = "heartbeat";

    public static final String LEADER = "leader";

    public static final String PLAYER = "Player";
    public static final String GROUP = "Group";
    public static final String TYPE = "type";

    public static final String ONLINE = "ONLINE";
    public static final String OFFLINE = "OFFLINE";

    public static final String STATE = "state";
    public static final String PLAY = "play";
    public static final String PAUSE = "pause";
    public static final String STOP = "stop";
    public static final String ON = "on";
    public static final String OFF = "off";
    public static final String MUTE = "mute";
    public static final String VOLUME = "volume";
    public static final String SONG = "song";
    public static final String ALBUM = "album";
    public static final String ARTIST = "artist";
    public static final String STATION = "station";
    public static final String IMAGE_URL = "image_url";
    public static final String CUR_POS = "curPos";
    public static final String DURATION = "duration";
    public static final String RAW_COMMAND = "rawCommand";
    public static final String PLAY_URL = "play_stream";

    public static final String NAME_HASH = "nameHash";
    public static final String GROUP_MEMBER_HASH = "groupMemberHash";
    public static final String GROUP_MEMBER_PID_LIST = "groupMemberPidList";

    public static final Set<@NonNull ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.unmodifiableSet(
            Stream.of(THING_TYPE_BRIDGE, THING_TYPE_GROUP, THING_TYPE_PLAYER).collect(Collectors.toSet()));

    // public static final Set<@NonNull ThingTypeUID> supportedThingTypes() {
    // Set<ThingTypeUID> supportedThings = Sets.newHashSet(THING_TYPE_BRIDGE, THING_TYPE_GROUP, THING_TYPE_PLAYER);
    // return supportedThings;
    // }
}
