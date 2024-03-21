package com.sgasecurity.api;


import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "site_timed_events")
public class SiteTimedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "timestamp", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date timestamp;

    @Column(name = "unique_site_id")
    private String uniqueSiteId;

    @Column(name = "reference_trigger_event")
    private String referenceTriggerEvent;

    @Column(name = "reference_trigger_date_time")
    private Date referenceTriggerDateTime;

    @Column(name = "last_timed_event_executed_name")
    private String lastTimedEventExecutedName;

    @Column(name = "last_timed_event_executed_datetime")
    private Date lastTimedEventExecutedDatetime;

    @Column(name = "last_timed_event_numeric_position")
    private Integer lastTimedEventNumericPosition;

    @Column(name = "month_counter", nullable = false)
    private Integer monthCounter;

    @Column(name = "is_paid")
    private String isPaid;

    @Column(name = "notes")
    private String notes;

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

    public void setUniqueSiteId(String uniqueSiteId) {
        this.uniqueSiteId = uniqueSiteId;
    }

    public String getUniqueSiteId() {
        return uniqueSiteId;
    }

    public void setReferenceTriggerEvent(String referenceTriggerEvent) {
        this.referenceTriggerEvent = referenceTriggerEvent;
    }

    public String getReferenceTriggerEvent() {
        return referenceTriggerEvent;
    }

    public void setReferenceTriggerDateTime(Date referenceTriggerDateTime) {
        this.referenceTriggerDateTime = referenceTriggerDateTime;
    }

    public Date getReferenceTriggerDateTime() {
        return referenceTriggerDateTime;
    }

    public void setLastTimedEventExecutedName(String lastTimedEventExecutedName) {
        this.lastTimedEventExecutedName = lastTimedEventExecutedName;
    }

    public String getLastTimedEventExecutedName() {
        return lastTimedEventExecutedName;
    }

    public void setLastTimedEventExecutedDatetime(Date lastTimedEventExecutedDatetime) {
        this.lastTimedEventExecutedDatetime = lastTimedEventExecutedDatetime;
    }

    public Date getLastTimedEventExecutedDatetime() {
        return lastTimedEventExecutedDatetime;
    }

    public void setLastTimedEventNumericPosition(Integer lastTimedEventNumericPosition) {
        this.lastTimedEventNumericPosition = lastTimedEventNumericPosition;
    }

    public Integer getLastTimedEventNumericPosition() {
        return lastTimedEventNumericPosition;
    }

    public void setMonthCounter(Integer monthCounter) {
        this.monthCounter = monthCounter;
    }

    public Integer getMonthCounter() {
        return monthCounter;
    }

    public void setIsPaid(String isPaid) {
        this.isPaid = isPaid;
    }

    public String getIsPaid() {
        return isPaid;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }


    @Override
    public String toString() {
        return "SiteTimedEvent{" +
                "id=" + id + '\'' +
                "timestamp=" + timestamp + '\'' +
                "uniqueSiteId=" + uniqueSiteId + '\'' +
                "referenceTriggerEvent=" + referenceTriggerEvent + '\'' +
                "referenceTriggerDateTime=" + referenceTriggerDateTime + '\'' +
                "lastTimedEventExecutedName=" + lastTimedEventExecutedName + '\'' +
                "lastTimedEventExecutedDatetime=" + lastTimedEventExecutedDatetime + '\'' +
                "lastTimedEventNumericPosition=" + lastTimedEventNumericPosition + '\'' +
                "monthCounter=" + monthCounter + '\'' +
                "notes=" + notes + '\'' +
                '}';
    }
}