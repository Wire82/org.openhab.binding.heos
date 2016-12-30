package org.openhab.binding.heos.resources;

import java.util.HashMap;

public class HeosResponseEvent {
	//RAW Values filled by Gson not decoded more or less for information
	private String command = null;	
	private String result = null;
	private String message = null; 
	
	//Values evaluated from Gson filled Values
	private String commandType = null;
	private String eventType = null;
	private HashMap<String,String> messagesMap = null;
	
	@Override
	public String toString() {
		return commandType;
	}
	
	public void getInfos () {
		System.out.println( "\n\nEvent Type: " + eventType + "\nCommand: "+ commandType);
		if (message != null) {
			for (String key : messagesMap.keySet()) {
			System.out.println(key + ": " + messagesMap.get(key));
			}
		}
	}
	

	public String getCommand() {
		return command;
	}

	public String getResult() {
		return result;
	}

	public String getMessage() {
		return message;
	}

	public String getCommandType() {
		return commandType;
	}

	public String getEventType() {
		return eventType;
	}

	public HashMap<String, String> getMessagesMap() {
		return messagesMap;
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

}
