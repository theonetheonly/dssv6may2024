package com.sgasecurity.api;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(name = "temp_handed_over_customers_today")
public class TempHandedoverCustomersToday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "system_customer_no")
    private String systemCustomerNo;
    @Column(name = "surname")
    private String surname;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "id_passport")
    private String idPassport;
    @Column(name = "kra_pin")
    private String kraPin;
    @Column(name = "phone")
    private String phone;
    @Column(name = "email")
    private String email;
    @Column(name = "estate_name")
    private String estateName;
    @Column(name = "road_street")
    private String roadStreet;
    @Column(name = "town_city")
    private String townCity;
    @Column(name = "postal_code")
    private String postalCode;
    @Column(name = "postal_address")
    private String postalAddress;
    @Column(name = "home_type")
    private String homeType;
    @Column(name = "household_number")
    private int householdNumber;
    @Column(name = "package_type")
    private String packageType;
    @Column(name = "handover_date")
    private LocalDate handoverDate;
    @Column(name = "timestamp")
    private Timestamp timestamp;

    public TempHandedoverCustomersToday() {
    }

    public TempHandedoverCustomersToday(int id, String systemCustomerNo, String firstName, String surname, String phone, String email, String postalAddress, String kraPin, String idPassport, String estateName, String roadStreet, String townCity, String postalCode, String homeType, int householdNumber, String packageType, LocalDate handoverDate, Timestamp timestamp) {
        this.id = id;
        this.systemCustomerNo = systemCustomerNo;
        this.firstName = firstName;
        this.surname = surname;
        this.phone = phone;
        this.email = email;
        this.postalAddress = postalAddress;
        this.kraPin = kraPin;
        this.idPassport = idPassport;
        this.estateName = estateName;
        this.roadStreet = roadStreet;
        this.townCity = townCity;
        this.postalCode = postalCode;
        this.homeType = homeType;
        this.householdNumber = householdNumber;
        this.packageType = packageType;
        this.handoverDate = handoverDate;
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
    public String getSystemCustomerNo()
    {
        return systemCustomerNo;
    }
    public void setSystemCustomerNo(String systemCustomerNo)
    {
        this.systemCustomerNo = systemCustomerNo;
    }
    public String getFirstName()
    {
        return firstName;
    }
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }
    public String getSurname()
    {
        return surname;
    }
    public void setSurname(String surname)
    {
        this.surname = surname;
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
    public String getPostalAddress()
    {
        return postalAddress;
    }
    public void setPostalAddress(String postalAddress)
    {
        this.postalAddress = postalAddress;
    }
    public String getKraPin()
    {
        return kraPin;
    }
    public void setKraPin(String kraPin)
    {
        this.kraPin = kraPin;
    }
    public String getIdPassport()
    {
        return idPassport;
    }
    public void setIdPassport(String idPassport)
    {
        this.idPassport = idPassport;
    }
    public String getEstateName()
    {
        return estateName;
    }
    public void setEstateName(String estateName)
    {
        this.estateName = estateName;
    }
    public String getRoadStreet()
    {
        return roadStreet;
    }
    public void setRoadStreet(String roadStreet)
    {
        this.roadStreet = roadStreet;
    }
    public String getTownCity()
    {
        return townCity;
    }
    public void setTownCity(String townCity)
    {
        this.townCity = townCity;
    }
    public String getPostalCode()
    {
        return postalCode;
    }
    public void setPostalCode(String postalCode)
    {
        this.postalCode = postalCode;
    }
    public String getHomeType()
    {
        return homeType;
    }
    public void setHomeType(String homeType)
    {
        this.homeType = homeType;
    }
    public int getHouseholdNumber()
    {
        return householdNumber;
    }
    public void setHouseholdNumber(int householdNumber)
    {
        this.householdNumber = householdNumber;
    }
    public String getPackageType()
    {
        return packageType;
    }
    public void setPackageType(String packageType)
    {
        this.packageType = packageType;
    }

    public LocalDate getHandoverDate()
    {
        return handoverDate;
    }
    public void setHandoverDate(LocalDate handoverDate)
    {
        this.handoverDate = handoverDate;
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
