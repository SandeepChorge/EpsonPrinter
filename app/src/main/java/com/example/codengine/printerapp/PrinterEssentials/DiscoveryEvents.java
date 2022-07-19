package com.example.codengine.printerapp.PrinterEssentials;

public class DiscoveryEvents{
    public boolean isDiscovering = false;
    public DisEventEnum eventEnum;
    public boolean iserror = false;
    public String result = "";

    DiscoveryEvents(){

    }

    public boolean isDiscovering() {
        return isDiscovering;
    }

    public void setDiscovering(boolean discovering) {
        isDiscovering = discovering;
    }

    public DisEventEnum getEventEnum() {
        return eventEnum;
    }

    public void setEventEnum(DisEventEnum eventEnum) {
        this.eventEnum = eventEnum;
    }

    public boolean isIserror() {
        return iserror;
    }

    public void setIserror(boolean iserror) {
        this.iserror = iserror;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
