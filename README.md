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

_Describe the available auto-discovery features here. Mention for what it works and what needs to be kept in mind when using it._

## Binding Configuration
This binding does not require any configuration via a .cfg file. The configuration is done via the Thing definition.

## Thing Configuration
It is recommended to configure 

_Describe what is needed to manually configure a thing, either through the (Paper) UI or via a thing-file. This should be mainly about its mandatory and optional configuration parameters. A short example entry for a thing file can help!_

_Note that it is planned to generate some part of this based on the XML files within ```ESH-INF/thing``` of your binding._

## Channels

_Here you should provide information about available channel types, what their meaning is and how they can be used._

_Note that it is planned to generate some part of this based on the XML files within ```ESH-INF/thing``` of your binding._

## Full Example

_Provide a full usage example based on textual configuration files (*.things, *.items, *.sitemap)._

## Any custom content here!

_Feel free to add additional sections for whatever you think should also be mentioned about your binding!_
