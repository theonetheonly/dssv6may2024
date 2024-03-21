package com.sgasecurity.api;


import org.springframework.web.bind.annotation.RestController;

@RestController
public class Data {
    public String event;
    public String latitude;
    public String longitude;
    public String measuredAt;

    public Data() {
    }

    public Data(String event, String latitude, String longitude, String measuredAt) {
        this.event = event;
        this.latitude = latitude;
        this.longitude = longitude;
        this.measuredAt = measuredAt;
    }

    public String getEvent() {
        return event;
    }
    public void setEvent(String event) {
        this.event = event;
    }

    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getMeasuredAt() {
        return measuredAt;
    }
    public void setMeasuredAt(String measuredAt) {
        this.measuredAt = measuredAt;
    }
}

