package com.sgasecurity.api;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "hik_events")
public class HikEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "hpp_site_id")
    private String hppSiteId;
    @Column(name = "unique_site_id")
    private String uniqueSiteId;
    @Column(name = "hik_formatted_customer_site_name")
    private String hikFormattedCustomerSiteName;
    @Column(name = "system_customer_no")
    private String systemCustomerNo;
    @Column(name = "event_response")
    private boolean eventResponse;
    @Column(name = "iprp_response")
    private String iprpResponse;
    @Column(name = "event_type")
    private String eventType;
    @Column(name = "timestamp")
    private Timestamp timestamp;

    public HikEvent() {
    }

    public HikEvent(long id, String hppSiteId, String uniqueSiteId, String hikFormattedCustomerSiteName, String systemCustomerNo, boolean eventResponse, String iprpResponse, String eventType, Timestamp timestamp) {
        this.id = id;
        this.hppSiteId = hppSiteId;
        this.uniqueSiteId = uniqueSiteId;
        this.hikFormattedCustomerSiteName = hikFormattedCustomerSiteName;
        this.systemCustomerNo = systemCustomerNo;
        this.eventResponse = eventResponse;
        this.iprpResponse = iprpResponse;
        this.eventType = eventType;
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
    public String getSystemCustomerNo()
    {
        return systemCustomerNo;
    }
    public void setSystemCustomerNo(String systemCustomerNo)
    {
        this.systemCustomerNo = systemCustomerNo;
    }
    public String getHppSiteId(){ return hppSiteId; }
    public void setHppSiteId(String hppSiteId)
    {
        this.hppSiteId = hppSiteId;
    }
    public String getUniqueSiteId(){ return uniqueSiteId; }
    public void setUniqueSiteId(String uniqueSiteId)
    {
        this.uniqueSiteId = uniqueSiteId;
    }
    public String getHikFormattedCustomerSiteName(){ return hikFormattedCustomerSiteName; }
    public void setHikFormattedCustomerSiteName(String hikFormattedCustomerSiteName)
    {
        this.hikFormattedCustomerSiteName = hikFormattedCustomerSiteName;
    }
    public boolean getEventResponse(){ return eventResponse; }
    public void setEventResponse(boolean eventResponse)
    {
        this.eventResponse = eventResponse;
    }
    public String getIprpResponse(){ return iprpResponse; }
    public void setIprpResponse(String iprpResponse)
    {
        this.iprpResponse = iprpResponse;
    }
    public String getEventType(){ return eventType; }
    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }
    public Timestamp getTimestamp()
    {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp)
    {
        this.timestamp = timestamp;
    }
}

