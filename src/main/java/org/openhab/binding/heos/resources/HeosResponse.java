package org.openhab.binding.heos.resources;

public class HeosResponse {

    private HeosResponseEvent event;
    private HeosResponsePayload payload;
    private String pid = "0";

    public HeosResponse() {

        this.event = new HeosResponseEvent();
        this.payload = new HeosResponsePayload();

    }

    public HeosResponseEvent getEvent() {
        return event;
    }

    public HeosResponsePayload getPayload() {
        return payload;
    }

    public void setEvent(HeosResponseEvent event) {
        this.event = event;
    }

    public void setPayload(HeosResponsePayload payload) {
        this.payload = payload;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

}
