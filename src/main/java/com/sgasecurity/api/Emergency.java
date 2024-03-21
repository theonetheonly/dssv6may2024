package com.sgasecurity.api;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "emergencies")
public class Emergency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "eventid")
    private String eventId;
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
    @Column(name = "rescue_response_status")
    private int rescueResponseStatus;
    @Column(name = "rescue_response_headers")
    private String rescueResponseHeaders;
    @Column(name = "rescue_response_body")
    private String rescueResponseBody;
    @Column(name = "rescue_caller_id")
    private String rescueCallerId;
    @Column(name = "rescue_call_status")
    private String rescueCallStatus;
    @Column(name = "timestamp")
    private Timestamp timestamp;

    public Emergency() {
    }

    public Emergency(long id, String eventId, String hppSiteId, String systemCustomerNo, String latitude, String longitude, String measuredAt, int rescueResponseStatus, String rescueResponseHeaders, String rescueResponseBody, String rescueCallerId, String rescueCallStatus, Timestamp timestamp) {
        this.id = id;
        this.eventId = eventId;
        this.hppSiteId = hppSiteId;
        this.systemCustomerNo = systemCustomerNo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.measuredAt = measuredAt;
        this.rescueResponseStatus = rescueResponseStatus;
        this.rescueResponseHeaders = rescueResponseHeaders;
        this.rescueResponseBody = rescueResponseBody;
        this.rescueCallerId = rescueCallerId;
        this.rescueCallStatus = rescueCallStatus;
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
    public int getRescueResponseStatus()
    {
        return rescueResponseStatus;
    }
    public void setRescueResponseStatus(int rescueResponseStatus)
    {
        this.rescueResponseStatus = rescueResponseStatus;
    }
    public String getRescueResponseHeaders()
    {
        return rescueResponseHeaders;
    }
    public void setRescueResponseHeaders(String rescueResponseHeaders)
    {
        this.rescueResponseHeaders = rescueResponseHeaders;
    }
    public String getRescueResponseBody()
    {
        return rescueResponseBody;
    }
    public void setRescueResponseBody(String rescueResponseBody)
    {
        this.rescueResponseBody = rescueResponseBody;
    }
    public String getRescueCallerId()
    {
        return rescueCallerId;
    }
    public void setRescueCallerId(String rescueCallerId)
    {
        this.rescueCallerId = rescueCallerId;
    }
    public String getRescueCallStatus()
    {
        return rescueCallStatus;
    }
    public void setRescueCallStatus(String rescueCallStatus)
    {
        this.rescueCallStatus = rescueCallStatus;
    }
    public Timestamp getTimestamp()
    {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}