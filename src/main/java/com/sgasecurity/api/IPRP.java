package com.sgasecurity.api;

public class IPRP {
    public String key;
    public String deviceIndex;
    public String deviceName;
    public String accountID;
    public String status;
    public String username;
    public String pwd;

    public IPRP() {
    }

    public IPRP(String key, String deviceIndex, String deviceName, String accountID, String status, String username, String pwd) {
        this.key = key;
        this.deviceIndex = deviceIndex;
        this.deviceName = deviceName;
        this.accountID = accountID;
        this.status = status;
        this.username = username;
        this.pwd = pwd;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public String getDeviceIndex() {
        return deviceIndex;
    }
    public void setDeviceIndex(String deviceIndex) {
        this.deviceIndex = deviceIndex;
    }

    public String getDeviceName() {
        return deviceName;
    }
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getAccountID() {
        return accountID;
    }
    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPwd() {
        return pwd;
    }
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
