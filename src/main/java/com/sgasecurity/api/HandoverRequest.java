package com.sgasecurity.api;

public class HandoverRequest {
    private String application;
    private String uniquesiteid;
    private String data;

    public HandoverRequest(){}

    public HandoverRequest(String application, String uniquesiteid, String data){
        this.application = application;
        this.uniquesiteid = uniquesiteid;
        this.data = data;
    }

    public String getApplication()
    {
        return application;
    }
    public void setApplication(String application)
    {
        this.application = application;
    }
    public String getUniqueSiteId()
    {
        return uniquesiteid;
    }
    public void setUniqueSiteId(String uniqueSiteId)
    {
        this.uniquesiteid = uniquesiteid;
    }

    public String getData()
    {
        return data;
    }
    public void setData(String data)
    {
        this.data = data;
    }
}
