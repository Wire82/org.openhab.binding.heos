package org.openhab.binding.heos.resources;

import java.util.EventListener;

public interface HeosEventListener extends EventListener {

    void playerStateChangeEvent(String pid, String event, String command);

}
