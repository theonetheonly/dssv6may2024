package com.sgasecurity.api;

import java.time.LocalDateTime;

public class HandoverRequestData {
    private String event;
    private String action;
    private String timestamp;
    private String extra;

    public HandoverRequestData(){}

    public HandoverRequestData(String event, String action, String timestamp, String extra){
        this.event = event;
        this.action = action;
        this.timestamp = timestamp;
        this.extra = extra;
    }

    public String getEvent()
    {
        return event;
    }
    public void setEvent(String event)
    {
        this.event = event;
    }

    public String getAction()
    {
        return action;
    }
    public void setAction(String action)
    {
        this.action = action;
    }

    public String getTimestamp()
    {
        return timestamp;
    }
    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getExtra()
    {
        return extra;
    }
    public void setExtra(String extra)
    {
        this.extra = extra;
    }
}
