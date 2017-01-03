package org.openhab.binding.heos.resources;

import java.util.EventListener;
import java.util.HashMap;

public interface HeosEventListener extends EventListener {

    void playerStateChangeEvent(String pid, String event, String command);

    void playerMediaChangeEvent(String pid, HashMap<String, String> info);

    void bridgeChangeEvent(String event, String command);

}
