# org.openhab.binding.heos
Heos Binding for OpenHab


{% include base.html %}

# <bindingName> Binding
This binding support the HEOS-System from Denon for OpenHab 2. The binding provides basic control for the player and groups of the network. It also supports selecting favorites and play them on several players or groups on the HEOS-Network. 
The binding first establish a connection to one of the player of the HEOS-Network and use them as a bridge. After a connection is established, the binding searches for all player and groupd via the bridge. To keep the network traffic low it is recommended to establish only one bridge. Connection to the bridge is done via a Telnet connection.

## Supported Things

Bridge:
The binding support a bride for connecting to the HEOS-Network

Player:
A generic player is supported via this binding. Currently no differences are made between the players. May be introduced in the future

Groups:
Groups are supported by this bindung.


## Discovery

This binding support full automatic discovery of bridges, players and groups. It is recommended to use the PaperUI to setup the system and add all players and groups.
The bridge is discovered through UPnP in the local network. Once it is added the players and groups are read via the bridge and placed within the inbox.
Nethertheless also manual configuration is possible

## Binding Configuration
This binding does not require any configuration via a .cfg file. The configuration is done via the Thing definition.

## Thing Configuration
It is recommended to configure the things via the PaperUI or HABmin

### Bridge Configuration
The bridge can be added via the PaperUI. After adding the bridge the username and password can set by editing the thing via the PaperUI. For manual configuration the following parameter can be defined. The ipAddress has to be defined. All other fiels are optional.
````
Bridge heos:bridge:main "name" [ipAddress="192.168.0.1", name="Default", unserName"xxx", password="123456"]  
````

### Player Configuration
Player can be added via the PaperUI. All field are then filld automatically.
For manual configuration the player is defined as followed:
````
Thing heos:player:pid "name" [pid="123456789", name="name", model="modelName", ipAdress="192.168.0.xxx"] 

````
Pid behind the heos:player:--- should be changed as required. Every name or value can be used. It is recommended to use the player Pid. Within the configuration the pid field is mendetory. The rest is not required.

### Group Configuration
TBD

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
the channel have different paths if you configure our Things manual or via an UI. It is recommended to check the correct path via an UI.


### Player provide the following channels:

Channel Type ID | Item Type | Description
----------------|-----------|-------------
control | Player | Provides: Play / Pause / Next / Previous
volume | Dimmer | Volume control
mute | Switch | Mute the Player
titel | String | Song Title
interpret | String | Song Interpret
album | String  | Album Title

Sample:

```
Player LivingRoom_Control "Control" {channel="heos:player:main:LivinRoom:Control"}
```

### The Bridge provide the following channels:

Channel Type ID | Item Type | Description
----------------|-----------|-------------
reboot | Switch | Reboot the whole HEOS System. Can be used if you get in trouble with the system
dynamicGroupHandling | Switch | If this option id activated the system automatically removes groups if they are ungrouped. Only works if the group is added via an UI.
buildGroup | Switch | Is used to define a group. The player which shall be grouped has to be selected first. If Switch is then activated the group is build.

Also th bridge supports dynamic channeld which represent the player of the network and the favorites. They are dynamically added if player are found and if favorites are defined within the HEOS Account. To activate Favorites the system has to be signed in to the HEOS Account.

### Favorite Channels
Channel Type ID | Item Type | Description
----------------|-----------|-------------
 {mid} | Switch | A channel which represents the favorite. Please check via UI how the correct Channel Type looks like. (Experimental)
 
 ### Example
 ```
 
 ```

### Player Channels
Channel Type ID | Item Type | Description
----------------|-----------|-------------
{player Name} | Switch | A channel which represents the player. Please check via UI how the correct Channel Type looks like. (Experimental)

## Full Example

_Provide a full usage example based on textual configuration files (*.things, *.items, *.sitemap)._

## Any custom content here!

_Feel free to add additional sections for whatever you think should also be mentioned about your binding!_
