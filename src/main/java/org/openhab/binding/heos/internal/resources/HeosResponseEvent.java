/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.heos.internal.resources;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link HeosResponseEvent} returns the event part of the
 * JSON message from the HEOS network
 *
 * @author Johannes Einig - Initial contribution
 */

public class HeosResponseEvent {
    // RAW Values filled by Gson not decoded more or less for information
    private String command;
    private String result;
    private String message;

    // Values evaluated from Gson filled Values
    private String commandType;
    private String eventType;
    private HashMap<String, String> messagesMap = null;

    // Error values
    private String errorCode;
    private String errorMessage;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String toString() {
        return commandType;
    }

    public void getInfos() {
        logger.debug("Event Type: {} \n Command: {}", eventType, commandType);
        // System.out.println("\n\nEvent Type: " + eventType + "\nCommand: " + commandType);
        if (message != null) {
            for (String key : messagesMap.keySet()) {
                logger.debug("{} : {}", key, messagesMap.get(key));
                // System.out.println(key + ": " + messagesMap.get(key));
            }
        }
    }

    /**
     * Returns the raw message received by the
     * HEOS system.
     *
     * @return the un-decoded command from the HOES system
     */
    public String getCommand() {
        return command;
    }

    /**
     * Returns the result information of the HEOS message
     *
     * @return either "success" or "fail"
     */
    public String getResult() {
        return result;
    }

    /**
     * Returns the not decoded message of the JSON response
     * from the HEOS system
     *
     * @return the un-decoded message from the HEOS message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @return the command type of the HEOS message
     */
    public String getCommandType() {
        return commandType;
    }

    /**
     *
     * @return the event type of the HEOS message
     */

    public String getEventType() {
        return eventType;
    }

    /**
     * This returns a HashMap which contains all encoded messages
     * received by the HEOS JSON response.
     * Each command can be called by its key
     *
     * @return a map with all messages
     */

    public HashMap<String, String> getMessagesMap() {
        return messagesMap;
    }

    /**
     *
     * @return the HOES system error code
     */

    public String getErrorCode() {
        return errorCode;
    }

    /**
     *
     * @return the HEOS system error message
     */

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setMessagesMap(HashMap<String, String> messagesMap) {
        this.messagesMap = messagesMap;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
