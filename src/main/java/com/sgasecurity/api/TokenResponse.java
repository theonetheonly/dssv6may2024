package com.sgasecurity.api;

public class TokenResponse {
    private String token;
    private String expires;
    private String error;

    public TokenResponse() {
    }

    public TokenResponse(String token, String expires, String error) {
        this.token = token;
        this.expires = expires;
        this.error = error;
    }

    public String getToken()
    {
        return token;
    }
    public void setToken(String token)
    {
        this.token = token;
    }

    public String getExpires()
    {
        return expires;
    }
    public void setExpires(String expires)
    {
        this.expires = expires;
    }

    public String getError()
    {
        return error;
    }
    public void setError(String error)
    {
        this.error = error;
    }
}

