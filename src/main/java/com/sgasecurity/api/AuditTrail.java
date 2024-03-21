package com.sgasecurity.api;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "audit_trail")
public class AuditTrail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "context_name")
    private String contextName;
    @Column(name = "context_desc")
    private String contextDesc;
    @Column(name = "context_value")
    private String contextValue;
    @Column(name = "timestamp")
    private Timestamp timestamp;

    public AuditTrail() {
    }

    public AuditTrail(int id, String contextName, String contextDesc, String contextValue, Timestamp timestamp) {
        this.id = id;
        this.contextName = contextName;
        this.contextDesc = contextDesc;
        this.contextValue = contextValue;
        this.timestamp = timestamp;
    }

    public int getId()
    {
        return id;
    }
    public void setId(int id)
    {
        this.id = id;
    }
    public String getContextName()
    {
        return contextName;
    }
    public void setContextName(String contextName)
    {
        this.contextName = contextName;
    }
    public String getContextDesc()
    {
        return contextDesc;
    }
    public void setContextDesc(String contextDesc)
    {
        this.contextDesc = contextDesc;
    }
    public String getContextValue()
    {
        return contextValue;
    }
    public void setContextValue(String contextValue)
    {
        this.contextValue = contextValue;
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
