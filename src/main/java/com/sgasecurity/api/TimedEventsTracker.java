package com.sgasecurity.api;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "timed_events_tracker")
public class TimedEventsTracker implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "timestamp", nullable = true)
    private Date timestamp;

    @Column(name = "context_name")
    private String contextName;

    @Column(name = "context_value")
    private String contextValue;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public String getContextName() {
        return contextName;
    }

    public void setContextValue(String contextValue) {
        this.contextValue = contextValue;
    }

    public String getContextValue() {
        return contextValue;
    }

    @Override
    public String toString() {
        return "TimedEventsTracker{" +
                "id=" + id + '\'' +
                "timestamp=" + timestamp + '\'' +
                "contextName=" + contextName + '\'' +
                "contextValue=" + contextValue + '\'' +
                '}';
    }
}
