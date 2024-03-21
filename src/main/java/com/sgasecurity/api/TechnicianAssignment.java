package com.sgasecurity.api;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(name = "technician_assignment")
public class TechnicianAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "technician_id")
    private int technicianId;
    @Column(name = "unique_site_id")
    private String uniqueSiteId;
    @Column(name = "reminder_date")
    private LocalDate reminderDate;
    @Column(name = "timestamp")
    private Timestamp timestamp;

    public TechnicianAssignment() {
    }

    public TechnicianAssignment(int id, int technicianId, String uniqueSiteId, LocalDate reminderDate, Timestamp timestamp) {
        this.id = id;
        this.technicianId = technicianId;
        this.uniqueSiteId = uniqueSiteId;
        this.reminderDate = reminderDate;
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
    public int getTechnicianId()
    {
        return technicianId;
    }
    public void setTechnicianId(int technicianId)
    {
        this.technicianId = technicianId;
    }

    public String getUniqueSiteId()
    {
        return uniqueSiteId;
    }
    public void setUniqueSiteId(String uniqueSiteId)
    {
        this.uniqueSiteId = uniqueSiteId;
    }
    public LocalDate getReminderDate()
    {
        return reminderDate;
    }
    public void setReminderDate(LocalDate reminderDate)
    {
        this.reminderDate = reminderDate;
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