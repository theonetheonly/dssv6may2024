package com.sgasecurity.api;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "handover_events")
public class HandoverEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "eventid")
    private String eventId;
    @Column(name = "event")
    private String event;
    @Column(name = "hpp_site_id")
    private String hppSiteId;
    @Column(name = "system_customer_no")
    private String systemCustomerNo;
    @Column(name = "latitude")
    private String latitude;
    @Column(name = "longitude")
    private String longitude;
    @Column(name = "measured_at")
    private String measuredAt;
    @Column(name = "timestamp")
    private Timestamp timestamp;

    public HandoverEvent() {
    }

    public HandoverEvent(long id, String eventId, String event, String hppSiteId, String systemCustomerNo, String latitude, String longitude, String measuredAt, Timestamp timestamp) {
        this.id = id;
        this.eventId = eventId;
        this.event = event;
        this.hppSiteId = hppSiteId;
        this.systemCustomerNo = systemCustomerNo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.measuredAt = measuredAt;
        this.timestamp = timestamp;
    }

    public long getId()
    {
        return id;
    }
    public void setId(long id)
    {
        this.id = id;
    }
    public String getEventId()
    {
        return eventId;
    }
    public void setEventId(String eventId)
    {
        this.eventId = eventId;
    }
    public String getEvent()
    {
        return event;
    }
    public void setEvent(String event)
    {
        this.event = event;
    }
    public String getHppSiteId(){ return hppSiteId; }
    public void setHppSiteId(String hppSiteId)
    {
        this.hppSiteId = hppSiteId;
    }
    public String getSystemCustomerNo()
    {
        return systemCustomerNo;
    }
    public void setSystemCustomerNo(String systemCustomerNo)
    {
        this.systemCustomerNo = systemCustomerNo;
    }

    public String getLatitude()
    {
        return latitude;
    }
    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }

    public String getLongitude()
    {
        return longitude;
    }
    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }

    public String getMeasuredAt()
    {
        return measuredAt;
    }
    public void setMeasuredAt(String measuredAt)
    {
        this.measuredAt = measuredAt;
    }
    public Timestamp getTimestamp()
    {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
