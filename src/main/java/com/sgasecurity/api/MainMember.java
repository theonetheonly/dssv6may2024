package com.sgasecurity.api;

import java.util.List;

public class MainMember {
    private String id;
    private String formalFullName;
    private List<String> alternativeIdentifiers;
    private String dateOfBirth;
    private String sex;
    private String telephoneNumber;
    private String preferredHospitalText;
    private String insurerText;
    private String insurancePolicyNumber;

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormalFullName() {
        return formalFullName;
    }

    public void setFormalFullName(String formalFullName) {
        this.formalFullName = formalFullName;
    }

    public List<String> getAlternativeIdentifiers() {
        return alternativeIdentifiers;
    }

    public void setAlternativeIdentifiers(List<String> alternativeIdentifiers) {
        this.alternativeIdentifiers = alternativeIdentifiers;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getPreferredHospitalText() {
        return preferredHospitalText;
    }

    public void setPreferredHospitalText(String preferredHospitalText) {
        this.preferredHospitalText = preferredHospitalText;
    }

    public String getInsurerText() {
        return insurerText;
    }

    public void setInsurerText(String insurerText) {
        this.insurerText = insurerText;
    }

    public String getInsurancePolicyNumber() {
        return insurancePolicyNumber;
    }

    public void setInsurancePolicyNumber(String insurancePolicyNumber) {
        this.insurancePolicyNumber = insurancePolicyNumber;
    }
}

