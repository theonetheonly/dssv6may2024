package com.sgasecurity.api;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "last_job_no")
public class LastJobNo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "job_no")
    private String jobNo;
    @Column(name = "timestamp")
    private Timestamp timestamp;

    public LastJobNo() {
    }
    public LastJobNo(int id, String jobNo, Timestamp timestamp) {
        this.id = id;
        this.jobNo = jobNo;
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
    public String getJobNo()
    {
        return jobNo;
    }
    public void setJobNo(String jobNo)
    {
        this.jobNo = jobNo;
    }
    public Timestamp getTimestamp()
    {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}