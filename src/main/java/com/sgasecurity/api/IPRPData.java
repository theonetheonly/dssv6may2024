package com.sgasecurity.api;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "iprp_data")
public class IPRPData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "customer_id")
    private int customerId;
    @Column(name = "site_id")
    private String siteId;
    @Column(name = "site_name")
    private String siteName;
    @Column(name = "dev_name")
    private String devName;
    @Column(name = "account_id")
    private String accountId;
    @Column(name = "active_status")
    private String activeStatus;
    @Column(name = "dev_index")
    private String devIndex;
    @Column(name = "dev_serial")
    private String devSerial;
    @Column(name = "device_id")
    private String deviceId;
    @Column(name = "device_key")
    private String deviceKey;
    @Column(name = "timestamp")
    private Timestamp timestamp;

    public IPRPData() {
    }

    public IPRPData(long id, int customerId, String siteId, String siteName, String devName, String accountId, String activeStatus, String devIndex, String devSerial, String deviceId, String deviceKey, Timestamp timestamp) {
        this.id = id;
        this.customerId = customerId;
        this.siteId = siteId;
        this.siteName = siteName;
        this.devName = devName;
        this.accountId = accountId;
        this.activeStatus = activeStatus;
        this.devIndex = devIndex;
        this.devSerial = devSerial;
        this.deviceId = deviceId;
        this.deviceKey = deviceKey;
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
    public int getCustomerId()
    {
        return customerId;
    }
    public void setCustomerId(int customerId)
    {
        this.customerId = customerId;
    }
    public String getSiteID()
    {
        return siteId;
    }
    public void setSiteId(String siteId)
    {
        this.siteId = siteId;
    }
    public String getSiteName(){ return siteName; }
    public void setSiteName(String siteName)
    {
        this.siteName = siteName;
    }
    public String getDevName(){ return devName; }
    public void setDevName(String devName)
    {
        this.devName = devName;
    }
    public String getAccountId(){ return accountId; }
    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }
    public String getActiveStatus(){ return activeStatus; }
    public void setActiveStatus(String activeStatus)
    {
        this.activeStatus = activeStatus;
    }
    public String getDevIndex(){ return devIndex; }
    public void setDevIndex(String devIndex)
    {
        this.devIndex = devIndex;
    }
    public String getDevSerial(){ return devSerial; }
    public void setDevSerial(String devSerial)
    {
        this.devSerial = devSerial;
    }
    public String getDeviceId(){ return deviceId; }
    public void setDeviceId(String deviceId)
    {
        this.deviceId = deviceId;
    }
    public String getDeviceKey(){ return deviceKey; }
    public void setDeviceKey(String deviceKey)
    {
        this.deviceKey = deviceKey;
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


