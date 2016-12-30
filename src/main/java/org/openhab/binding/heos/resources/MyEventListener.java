package org.openhab.binding.heos.resources;

import java.util.ArrayList;

public class MyEventListener {

    protected ArrayList<HeosEventListener> listenerList = new ArrayList<HeosEventListener>();

    public void addListener(HeosEventListener listener) {

        listenerList.add(listener);

    }

    public void removeListener(HeosEventListener listener) {

        listenerList.remove(listener);
    }

    public void fireEvent(String pid, String event, String command) {

        // Debug
        // System.out.println("Fire");

        for (int i = 0; i < listenerList.size(); i++) {

            listenerList.get(i).playerStateChangeEvent(pid, event, command);

        }
    }

}
