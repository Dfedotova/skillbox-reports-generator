package com.example.application.data.entity;

import javax.persistence.Entity;

import com.example.application.data.AbstractEntity;

import java.time.LocalDate;

@Entity
public class SampleContractor extends AbstractEntity {

    private int id;
    private String lastName;
    private String firstName;
    private String secondName;
    private String contractorType;
    private String OOOForm;
    private String OOOName;
    private String taxPercentage;
    private String signatoryPosition;
    private LocalDate selfemployedDate;
    private String registrationCertificateNumber;
    private LocalDate registrationCertificateDate;
    private String registrationNumber;
    private String ITN;
    private String proxyNumber;
    private LocalDate proxyDate;

    public int getID() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName.trim();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName.trim();
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName.trim();
    }

    public String getContractorType() {
        return contractorType;
    }

    public void setContractorType(String contractorType) {
        this.contractorType = contractorType;
    }

    public String getOOOForm() {
        return OOOForm;
    }

    public void setOOOForm(String OOOForm) {
        this.OOOForm = OOOForm;
    }

    public String getOOOName() {
        return OOOName;
    }

    public void setOOOName(String OOOName) {
        if (OOOName != null)
            OOOName = OOOName.trim();
        this.OOOName = OOOName;
    }

    public String getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(String taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public String getSignatoryPosition() {
        return signatoryPosition;
    }

    public void setSignatoryPosition(String signatoryPosition) {
        this.signatoryPosition = signatoryPosition;
    }

    public LocalDate getSelfemployedDate() {
        return selfemployedDate;
    }

    public void setSelfemployedDate(LocalDate selfemployedDate) {
        this.selfemployedDate = selfemployedDate;
    }

    public String getRegistrationCertificateNumber() {
        return registrationCertificateNumber;
    }

    public void setRegistrationCertificateNumber(String registrationCertificateNumber) {
        this.registrationCertificateNumber = registrationCertificateNumber;
    }

    public LocalDate getRegistrationCertificateDate() {
        return registrationCertificateDate;
    }

    public void setRegistrationCertificateDate(LocalDate registrationCertificateDate) {
        this.registrationCertificateDate = registrationCertificateDate;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getITN() {
        return ITN;
    }

    public void setITN(String ITN) {
        this.ITN = ITN;
    }

    public String getProxyNumber() {
        return proxyNumber;
    }

    public void setProxyNumber(String proxyNumber) {
        this.proxyNumber = proxyNumber;
    }

    public LocalDate getProxyDate() {
        return proxyDate;
    }

    public void setProxyDate(LocalDate proxyDate) {
        this.proxyDate = proxyDate;
    }
}
