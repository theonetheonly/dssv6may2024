package com.sgasecurity.api;

public class DeviceRow {
    private String id;
    private String deviceName;
    private int deviceOnlineStatus;
    private String deviceSerial;
    private String deviceVersion;
    private int deviceCategory;
    private String deviceType;
    private int deviceSubCategory;
    private boolean isSubscribed;
    private String deviceAddTime;
    private String timeZone;
    private String siteID;
    private String siteName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getDeviceOnlineStatus() {
        return deviceOnlineStatus;
    }

    public void setDeviceOnlineStatus(int deviceOnlineStatus) {
        this.deviceOnlineStatus = deviceOnlineStatus;
    }

    public String getDeviceSerial() {
        return deviceSerial;
    }

    public void setDeviceSerial(String deviceSerial) {
        this.deviceSerial = deviceSerial;
    }

    public String getDeviceVersion() {
        return deviceVersion;
    }

    public void setDeviceVersion(String deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    public int getDeviceCategory() {
        return deviceCategory;
    }

    public void setDeviceCategory(int deviceCategory) {
        this.deviceCategory = deviceCategory;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public int getDeviceSubCategory() {
        return deviceSubCategory;
    }

    public void setDeviceSubCategory(int deviceSubCategory) {
        this.deviceSubCategory = deviceSubCategory;
    }

    public boolean getIsSubscribed() {
        return isSubscribed;
    }

    public void setIsSubscribed(boolean isSubscribed) {
        this.isSubscribed = isSubscribed;
    }

    public String getDeviceAddTime() {
        return deviceAddTime;
    }

    public void setDeviceAddTime(String deviceAddTime) {
        this.deviceAddTime = deviceAddTime;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getSiteID() {
        return siteID;
    }

    public void setSiteID(String siteID) {
        this.siteID = siteID;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }
}
