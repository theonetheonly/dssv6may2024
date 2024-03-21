package com.sgasecurity.api;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class APIRequestData {
    private String accesscode;
    private String application;
    private String hppsiteid;
    private String customerno;
//    private String manuallySetHandoverDate;
    public Data data;

    public APIRequestData() {
    }

    public APIRequestData(String accesscode, String application, String hppsiteid, String customerno, Data data) {
        this.accesscode = accesscode;
        this.application = application;
        this.hppsiteid = hppsiteid;
        this.customerno = customerno;
//        this.manuallySetHandoverDate = manuallySetHandoverDate;
        this.data = data;
    }

    public String getAccesscode()
    {
        return accesscode;
    }
    public void setAccesscode(String accesscode)
    {
        this.accesscode = accesscode;
    }

    public String getApplication()
    {
        return application;
    }
    public void setApplication(String application)
    {
        this.application = application;
    }

    public String getHppsiteid()
    {
        return hppsiteid;
    }
    public void setHppsiteid(String hppsiteid)
    {
        this.hppsiteid = hppsiteid;
    }

    public String getCustomerno()
    {
        return customerno;
    }
    public void setCustomerno(String customerno)
    {
        this.customerno = customerno;
    }

//    public String getManuallySetHandoverDate()
//    {
//        return manuallySetHandoverDate;
//    }
//    public void setManuallySetHandoverDate(String manuallySetHandoverDate)
//    {
//        this.manuallySetHandoverDate = manuallySetHandoverDate;
//    }

    public Data getData()
    {
        return data;
    }
    public void setData(Data data)
    {
        this.data = data;
    }
}
