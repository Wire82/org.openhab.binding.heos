/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.heos.internal.resources;

import java.util.EventListener;
import java.util.HashMap;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.heos.internal.api.HeosEventController;

/**
 * The {@link HeosEventListener } is an Event Listener
 * for the HEOS network. Handler which wants the get informed
 * by an HEOS event via the {@link HeosEventController} has to
 * implement this class and register itself at the {@link HeosEventController}
 *
 * @author Johannes Einig - Initial contribution
 */
@NonNullByDefault
public interface HeosEventListener extends EventListener {

    void playerStateChangeEvent(String pid, String event, String command);

    void playerMediaChangeEvent(String pid, HashMap<String, String> info);

    void bridgeChangeEvent(String event, String result, String command);

}
