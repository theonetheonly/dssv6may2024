package com.sgasecurity.api;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "technicians")
public class Technician {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "id_no")
    private String idNo;
    @Column(name = "phone")
    private String phone;
    @Column(name = "email")
    private String email;
    @Column(name = "sga_staff_no")
    private String SGAStaffNo;
    @Column(name = "photo")
    private String photo;
    @Column(name = "status")
    private String status;
    @Column(name = "timestamp")
    private Timestamp timestamp;

    public Technician() {
    }

    public Technician(int id, String firstName, String lastName, String idNo, String phone, String email, String SGAStaffNo, String photo, String status, Timestamp timestamp) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.idNo = idNo;
        this.phone = phone;
        this.email = email;
        this.SGAStaffNo = SGAStaffNo;
        this.photo = photo;
        this.status = status;
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
    public String getFirstName()
    {
        return firstName;
    }
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getIdNo()
    {
        return idNo;
    }
    public void setIdNo(String idNo)
    {
        this.idNo = idNo;
    }
    public String getPhone()
    {
        return phone;
    }
    public void setPhone(String phone)
    {
        this.phone = phone;
    }
    public String getEmail()
    {
        return email;
    }
    public void setEmail(String email)
    {
        this.email = email;
    }
    public String getSGAStaffNo()
    {
        return SGAStaffNo;
    }
    public void setSGAStaffNo(String SGAStaffNo)
    {
        this.SGAStaffNo = SGAStaffNo;
    }
    public String getStatus()
    {
        return status;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }
    public String getPhoto()
    {
        return photo;
    }
    public void setPhoto(String photo)
    {
        this.photo = photo;
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
