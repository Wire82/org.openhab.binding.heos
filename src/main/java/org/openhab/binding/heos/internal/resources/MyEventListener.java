/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.heos.internal.resources;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link MyEventListener } is used for classes which
 * wants to inform players or groups about change events
 * from the HEOS system. Classes which wants to be informed
 * has to implement the {@link HeosEventListener} and register at
 * the class which extends this {@link MyEventListener} *
 *
 * @author Johannes Einig - Initial contribution
 */

public class MyEventListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    protected ArrayList<HeosEventListener> listenerList = new ArrayList<HeosEventListener>();

    /**
     * Register a listener from type {@link HeosEventListener} to be notified by
     * a change event
     *
     * @param listener the lister from type {@link HeosEventListener} for change events
     */

    public void addListener(HeosEventListener listener) {
        listenerList.add(listener);
    }

    /**
     * Removes the listener from the notification list
     *
     * @param listener the listener from type {@link HeosEventListener} to be removed
     */

    public void removeListener(HeosEventListener listener) {
        listenerList.remove(listener);
    }

    /**
     * Notifies the registered listener of a changed state type event
     *
     * @param pid the ID of the player or group which has changed
     * @param event the name of the event (see {@link HeosConstants} for event types)
     * @param command the command of the event
     */

    public void fireStateEvent(String pid, String event, String command) {
        listenerList.forEach(element -> element.playerStateChangeEvent(pid, event, command));
    }

    /**
     * Notifies the registered listener of a changed media type event
     *
     * @param pid the ID of the player or group which has changed
     * @param info an HashMap which contains the media information
     */

    public void fireMediaEvent(String pid, HashMap<String, String> info) {
        listenerList.forEach(element -> element.playerMediaChangeEvent(pid, info));
    }

    /**
     * Notifies the registered listener if a change of the bridge state
     *
     * @param event the event type
     * @param result the result (success or fail)
     * @param command the command of the event
     */

    public void fireBridgeEvent(String event, String result, String command) {
        // logger.warn("Bridge Event with {} {} {} List is empty {}", event, result, command, listenerList.size());
        // Doesn't work. Throws an exception....
        // listenerList.forEach(element -> element.bridgeChangeEvent(event, result, command));
        for (int i = 0; i < listenerList.size(); i++) {
            listenerList.get(i).bridgeChangeEvent(event, result, command);
        }
    }
}
