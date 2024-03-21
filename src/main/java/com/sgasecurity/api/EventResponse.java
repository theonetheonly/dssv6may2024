package com.sgasecurity.api;

public class EventResponse {
    private String eventid;
    private String status;
    private String error;

    public EventResponse() {
    }

    public EventResponse(String eventid, String status, String error) {
        this.eventid = eventid;
        this.status = status;
        this.error = error;
    }

    public String getEventid()
    {
        return eventid;
    }
    public void setEventid(String eventid)
    {
        this.eventid = eventid;
    }

    public String getStatus()
    {
        return status;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getError()
    {
        return error;
    }
    public void setError(String error)
    {
        this.error = error;
    }
}
