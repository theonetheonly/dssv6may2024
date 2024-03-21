package com.sgasecurity.api;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "sos")
public class SOS {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "title")
    private String title;
    @Column(name = "system_customer_no")
    private String systemCustomerNo;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "middle_name")
    private String middleName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "phone")
    private String phone;
    @Column(name = "id_passport")
    private String idPassport;
    @Column(name = "email")
    private String email;
    @Column(name = "primary_contact")
    private String primaryContact;
    @Column(name = "house_door_no")
    private String houseDoorNo;
    @Column(name = "building")
    private String building;
    @Column(name = "locality")
    private String locality;
    @Column(name = "identification_type")
    private String identificationType;
    @Column(name = "occupation")
    private String occupation;
    @Column(name = "estate_name")
    private String estateName;
    @Column(name = "road_street")
    private String roadStreet;
    @Column(name = "postal_address")
    private String postalAddress;
    @Column(name = "town_city")
    private String townCity;
    @Column(name = "postal_code")
    private String postalCode;
    @Column(name = "home_type")
    private String homeType;
    @Column(name = "household_number")
    private int householdNumber;
    @Column(name = "house_owning")
    private String houseOwning;
    @Column(name = "terms")
    private String terms;
    @Column(name = "kra_pin")
    private String kraPin;
    @Column(name = "sga_terms_status")
    private String sgaTermsStatus;
    @Column(name = "sanlam_terms_status")
    private String sanlamTermsStatus;
    @Column(name = "rescue_terms_status")
    private String rescueTermsStatus;
    @Column(name = "sign_up_stage")
    private String signUpStage;
    @Column(name = "sign_up_status")
    private String signUpStatus;
    @Column(name = "account_status")
    private String accountStatus;
    @Column(name = "comments")
    private String comments;
    @Column(name = "gen_customer_no")
    private String genCustomerNo;
    @Column(name = "timestamp")
    private Timestamp timestamp;

    public SOS() {
    }

    public SOS(int id, String systemCustomerNo, String title, String firstName, String middleName, String lastName, String phone, String email, String primaryContact, String postalAddress, String houseDoorNo, String houseOwning, String building, String locality, String identificationType, String kraPin, String idPassport, String occupation, String estateName, String roadStreet, String townCity, String postalCode, String homeType, int householdNumber, String terms, String sgaTermsStatus, String sanlamTermsStatus, String rescueTermsStatus, String signUpStage, String accountStatus, String comments, String genCustomerNo, Timestamp timestamp) {
        this.id = id;
        this.systemCustomerNo = systemCustomerNo;
        this.title = title;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.primaryContact = primaryContact;
        this.postalAddress = postalAddress;
        this.houseDoorNo = houseDoorNo;
        this.houseOwning = houseOwning;
        this.building = building;
        this.locality = locality;
        this.identificationType = identificationType;
        this.kraPin = kraPin;
        this.idPassport = idPassport;
        this.occupation = occupation;
        this.estateName = estateName;
        this.roadStreet = roadStreet;
        this.townCity = townCity;
        this.postalCode = postalCode;
        this.homeType = homeType;
        this.householdNumber = householdNumber;
        this.terms = terms;
        this.sgaTermsStatus = sgaTermsStatus;
        this.sanlamTermsStatus = sanlamTermsStatus;
        this.rescueTermsStatus = rescueTermsStatus;
        this.signUpStage = signUpStage;
        this.accountStatus = accountStatus;
        this.comments = comments;
        this.genCustomerNo = genCustomerNo;
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
    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getFirstName()
    {
        return firstName;
    }
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getMiddleName()
    {
        return middleName;
    }
    public void setMiddleName(String middleName)
    {
        this.middleName = middleName;
    }

    public String getLastName()
    {
        return lastName;
    }
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
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
    public String getPrimaryContact()
    {
        return primaryContact;
    }
    public void setPrimaryContact(String primaryContact)
    {
        this.primaryContact = primaryContact;
    }
    public String getPostalAddress()
    {
        return postalAddress;
    }
    public void setPostalAddress(String postalAddress)
    {
        this.postalAddress = postalAddress;
    }
    public String getHouseDoorNo()
    {
        return houseDoorNo;
    }
    public void setHouseDoorNo(String houseDoorNo)
    {
        this.houseDoorNo = houseDoorNo;
    }

    public String getBuilding()
    {
        return building;
    }
    public void setBuilding(String building)
    {
        this.building = building;
    }
    public String getLocality()
    {
        return locality;
    }
    public void setLocality(String locality)
    {
        this.locality = locality;
    }
    public String getIdentificationType()
    {
        return identificationType;
    }
    public void setIdentificationType(String identificationType)
    {
        this.identificationType = identificationType;
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
    public String getOccupation()
    {
        return occupation;
    }
    public void setOccupation(String occupation)
    {
        this.occupation = occupation;
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
    public String getHouseOwning()
    {
        return houseOwning;
    }
    public void setHouseOwning(String houseOwning)
    {
        this.houseOwning = houseOwning;
    }
    public String getTerms()
    {
        return terms;
    }
    public void setTerms(String terms)
    {
        this.terms = terms;
    }
    public String getSgaTermsStatus()
    {
        return sgaTermsStatus;
    }
    public void setSgaTermsStatus(String sgaTermsStatus)
    {
        this.sgaTermsStatus = sgaTermsStatus;
    }
    public String getSanlamTermsStatus()
    {
        return sanlamTermsStatus;
    }
    public void setSanlamTermsStatus(String sanlamTermsStatus)
    {
        this.sanlamTermsStatus = sanlamTermsStatus;
    }
    public String getRescueTermsStatus()
    {
        return rescueTermsStatus;
    }
    public void setRescueTermsStatus(String rescueTermsStatus)
    {
        this.rescueTermsStatus = rescueTermsStatus;
    }
    public String getSignUpStage()
    {
        return signUpStage;
    }
    public void setSignUpStage(String signUpStage)
    {
        this.signUpStage = signUpStage;
    }
    public String getAccountStatus()
    {
        return accountStatus;
    }
    public void setAccountStatus(String accountStatus)
    {
        this.accountStatus = accountStatus;
    }
    public String getComments()
    {
        return comments;
    }
    public void setComments(String comments)
    {
        this.comments = comments;
    }

    public String getGenCustomerNo()
    {
        return genCustomerNo;
    }
    public void setGenCustomerNo(String genCustomerNo)
    {
        this.genCustomerNo = genCustomerNo;
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

