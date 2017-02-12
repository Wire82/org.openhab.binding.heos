# org.openhab.binding.heos
Heos Binding for OpenHab


{% include base.html %}

# <bindingName> Binding
This binding support the HEOS-System from Denon for OpenHab 2. The binding provides basic control for the player and groups of the network. It also supports selecting favorites and play them on several players or groups on the HEOS-Network. 
The binding first establish a connection to one of the player of the HEOS-Network and use them as a bridge. After a connection is established, the binding searches for all player and grouped via the bridge. To keep the network traffic low it is recommended to establish only one bridge. Connection to the bridge is done via a Telnet connection.

## Supported Things

Bridge:
The binding support a bride for connecting to the HEOS-Network

Player:
A generic player is supported via this binding. Currently no differences are made between the players. May be introduced in the future

Groups:
Groups are supported by this binding.


## Discovery

This binding support full automatic discovery of bridges, players and groups. It is recommended to use the PaperUI to setup the system and add all players and groups.
The bridge is discovered through UPnP in the local network. Once it is added the players and groups are read via the bridge and placed within the inbox.
Nether the less also manual configuration is possible

## Binding Configuration
This binding does not require any configuration via a .cfg file. The configuration is done via the Thing definition.

## Thing Configuration
It is recommended to configure the things via the PaperUI or HABmin

### Bridge Configuration
The bridge can be added via the PaperUI. After adding the bridge the username and password can set by editing the thing via the PaperUI. For manual configuration the following parameter can be defined. The ipAddress has to be defined. All other files are optional.
````
Bridge heos:bridge:main "name" [ipAddress="192.168.0.1", name="Default", unserName"xxx", password="123456"]  
````

### Player Configuration
Player can be added via the PaperUI. All fields are then filled automatically.
For manual configuration the player is defined as followed:
````
Thing heos:player:pid "name" [pid="123456789", name="name", model="modelName", ipAdress="192.168.0.xxx"] 
````
Pid behind the heos:player:--- should be changed as required. Every name or value can be used. It is recommended to use the player Pid. Within the configuration the pid field is mandatory. The rest is not required.

### Group Configuration
Same as for the Heos Player

### Defining Bridge and Players together

Defining Player and Bridge together. To ensure that the players and groups are attached to the bridge the definition can be like:

```
Bridge heos:bridge:main "Bridge" [ipAddress="192.168.0.1", name="Bridge", userName="userName", password="123456"] {
	
	player Kitchen "Kitchen"[pid="434523813", name="Kitchen"]
	player LivingRoom "Living Room"[pid="918797451", name="Living Room"]
  	player 813793755 "Bath Room"[pid="813793755", name="Bath Room"]
	
}
```

## Channels

Note:
The channel have different paths if you configure our Things manual or via an UI. It is recommended to check the correct path via an UI.


### Player provide the following channels:

Channel ID | Item Type | Description
----------------|-----------|-------------
Control | Player | Play (also ON) / Pause (also OFF) / Next / Previous
Volume | Dimmer | Volume control
Mute | Switch | Mute the Player
Titel | String | Song Title
Interpret | String | Song Interpret
Album | String  | Album Title
Image_URL | String |The URL where the cover can be found 
Inputs | String | The input to be switched to. Input values from HEOS protocol.


####Example:

```
Player LivingRoom_Control "Control" {channel="heos:player:main:LivinRoom:Control"}
```


Inputs depending on Player type (Date 12.02.2017):

Input name |
----------------|
aux_in_1
aux_in_2
aux_in_3
aux_in_4
aux1
aux2
aux3
aux4
aux5
aux6
aux7
line_in_1
line_in_2
line_in_3
line_in_4
coax_in_1
coax_in_2
optical_in_1
optical_in_2
hdmi_in_1
hdmi_arc_1
cable_sat
dvd
bluray
game
mediaplayer
cd
tuner
hdradio
tvaudio
phono




### Groups provide the following channels:

Channel ID | Item Type | Description
----------------|-----------|-------------
Control | Player | Play (also ON) / Pause (also OFF) / Next / Previous
Volume | Dimmer | Volume control
Mute | Switch | Mute the Group
Titel | String | Song Title
Interpret | String | Song Interpret
Album | String  | Album Title
Ungroup | Switch | Ungroup the group
Image_URL | String |The URL where the cover can be found
OnlineStatus | String | Indicates the status ONLINE or OFFLINE



### The Bridge provide the following channels:

Channel ID | Item Type | Description
----------------|-----------|-------------
Reboot | Switch | Reboot the whole HEOS System. Can be used if you get in trouble with the system
DynamicGroupHandling | Switch | If this option id activated the system automatically removes groups if they are ungrouped. Only works if the group is added via an UI.
BuildGroup | Switch | Is used to define a group. The player which shall be grouped has to be selected first. If Switch is then activated the group is build.
Playlists | String | Plays a Playlist on the prior selected Player Channel (see below) Playlists are identified by numbers. List can be found in the HEOS App


## **Experimental**

Also the bridge supports dynamic channels which represent the player of the network and the favorites. They are dynamically added if player are found and if favorites are defined within the HEOS Account. To activate Favorites the system has to be signed in to the HEOS Account.


### Favorite Channels
Channel ID | Item Type | Description
----------------|-----------|-------------
 {mid} | Switch | A channel which represents the favorite. Please check via UI how the correct Channel Type looks like. (Experimental)
 
 Example
 ```
 Switch Favorite_1 "Fav 1 [%s]" {channel="heos:bridge:main:s17492"}
 ```

### Player Channels
Channel ID | Item Type | Description
----------------|-----------|-------------
{player Name} | Switch | A channel which represents the player. Please check via UI how the correct Channel Type looks like. (Experimental)

Example
 ```
 Switch Player_1 "Player [%s]" {channel="heos:bridge:main:LivingRoom"} 
 ```
 
 **Note: Both functions are experimental. It seems at the moment that the dynamic channels are only work correctly if things are managed via UI.**

## Full Example

###demo.things:

```
Bridge heos:bridge:main "Bridge" [ipAddress="192.168.0.1", name="Bridge", userName="userName", password="123456"] {
	
	player Kitchen "Kitchen"[pid="434523813", name="Kitchen"]
	player LivingRoom "Living Room"[pid="918797451", name="Living Room"]
  	player 813793755 "Bath Room"[pid="813793755", name="Bath Room"]
	
}
```

###demo.items:

```
Player LivingRoom_Control "Control" {channel="heos:player:main:LivingRoom:Control"}
Switch LivingRoom_Mute "Mute"{channel="heos:player:main:LivingRoom:Mute"}
Dimmer LivingRoom_Volume "Volume" {channel="heos:player:main:LivingRoom:Volume"}
String LivingRoom_Title "Title [%s]" {channel="heos:player:main:LivingRoom:Titel"}
String LivingRoom_Interpret "Interpret [%s]" {channel="heos:player:main:LivingRoom:Interpret"}
String LivingRoom_Album "Album [%s]" {channel="heos:player:main:LivingRoom:Album"}

```

###demo.sitemap
```
   Frame label="Arbeitszimmer" {
    	Default item=LivingRoom_Control
    	Default item=LivingRoom_Mute
    	Default item=LivingRoom_Volume
    	Default item=LivingRoom_Title
    	Default item=LivingRoom_Interpret
    	Default item=LivingRoom_Album
    }
```


## Note
This is a very early state of the binding. It was stated as a private project for using at home and never for publishing. But hopefully it will reach a state where it can be included into OpenHab.
At the moment it may work by some functions are not well tested an can cause trouble. But enjoy testing and feel free to give feedback.
