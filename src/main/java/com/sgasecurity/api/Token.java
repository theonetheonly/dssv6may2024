package com.sgasecurity.api;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "token")
    private String token;
    @Column(name = "expires")
    private String expires;
    @Column(name = "timestamp")
    private Timestamp timestamp;

    public Token(){}

    public Token(long id, String token, String expires, Timestamp timestamp){
        this.id = id;
        this.token = token;
        this.expires =expires;
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
    public Timestamp getTimestamp()
    {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
