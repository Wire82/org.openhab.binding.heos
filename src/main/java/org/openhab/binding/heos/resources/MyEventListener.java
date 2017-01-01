package org.openhab.binding.heos.resources;

import java.util.ArrayList;
import java.util.HashMap;

public class MyEventListener {

    protected ArrayList<HeosEventListener> listenerList = new ArrayList<HeosEventListener>();

    public void addListener(HeosEventListener listener) {

        listenerList.add(listener);

    }

    public void removeListener(HeosEventListener listener) {

        listenerList.remove(listener);
    }

    public void fireStateEvent(String pid, String event, String command) {

        for (int i = 0; i < listenerList.size(); i++) {

            listenerList.get(i).playerStateChangeEvent(pid, event, command);

        }
    }

    public void fireMediaEvent(String pid, HashMap<String, String> info) {

        for (int i = 0; i < listenerList.size(); i++) {

            listenerList.get(i).playerMediaChangeEvent(pid, info);

        }
    }

}
