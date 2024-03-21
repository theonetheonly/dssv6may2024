package com.sgasecurity.api;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "new_months")
public class NewMonth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "unique_site_id")
    private String uniqueSiteId;
    @Column(name = "month_counter_paid")
    private int monthCounterPaid;
    @Column(name = "timestamp")
    private Timestamp timestamp;

    public NewMonth() {
    }

    public NewMonth(long id, String uniqueSiteId, int monthCounterPaid, Timestamp timestamp) {
        this.id = id;
        this.uniqueSiteId = uniqueSiteId;
        this.monthCounterPaid = monthCounterPaid;
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
    public String getUniqueSiteId()
    {
        return uniqueSiteId;
    }
    public void setUniqueSiteId(String uniqueSiteId)
    {
        this.uniqueSiteId = uniqueSiteId;
    }
    public int getMonthCounterPaid()
    {
        return monthCounterPaid;
    }
    public void setMonthCounterPaid(int monthCounterPaid)
    {
        this.monthCounterPaid = monthCounterPaid;
    }
    public Timestamp getTimestamp()
    {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
