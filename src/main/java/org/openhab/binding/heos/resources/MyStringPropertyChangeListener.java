package org.openhab.binding.heos.resources;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;



public class MyStringPropertyChangeListener {
	
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	public void addPropertyChangeListener (PropertyChangeListener propertyChangeListener) {
		
		this.pcs.addPropertyChangeListener(propertyChangeListener);
	}
	
	public void removePropertyChangeListener (PropertyChangeListener listener) {
		
		this.pcs.removePropertyChangeListener(listener);
	}
	
	private String value;
	
	public String getValue() {
		return value;
	}
	
	public void setValue (String newValue) {
		String oldValue = this.value;
		this.value = newValue;
		this.pcs.firePropertyChange("value", oldValue, newValue);
		value = null; //experimental
	}

}
