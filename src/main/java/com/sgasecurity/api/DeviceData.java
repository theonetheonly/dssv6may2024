package com.sgasecurity.api;

import com.sgasecurity.api.device.Data;

public class DeviceData {
    private Data data;
    private String errorCode;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
