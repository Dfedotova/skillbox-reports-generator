package com.example.application.data.entity;

import javax.persistence.Entity;

import com.example.application.data.AbstractEntity;

import java.time.LocalDate;

@Entity
public class SampleReport extends AbstractEntity {

    private int id;
    private String reportModel;
    private String courseCode;
    private String courseName;
    private String courseDirection;
    private String courseObject;
    private String contractor;
    private String royaltyPercentage;
    private String contractNumber;
    private LocalDate contractDate;
    private LocalDate transferDateOfRIA;
    private String k2;

    public int getID() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReportModel() {
        return reportModel;
    }

    public void setReportModel(String reportModel) {
        this.reportModel = reportModel;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseDirection() {
        return courseDirection;
    }

    public void setCourseDirection(String courseDirection) {
        this.courseDirection = courseDirection;
    }

    public String getCourseObject() {
        return courseObject;
    }

    public void setCourseObject(String courseObject) {
        this.courseObject = courseObject;
    }

    public String getContractor() {
        return contractor;
    }

    public void setContractor(String contractor) {
        this.contractor = contractor;
    }

    public String getRoyaltyPercentage() {
        return royaltyPercentage;
    }

    public void setRoyaltyPercentage(String royaltyPercentage) {
        if (royaltyPercentage.contains(","))
            royaltyPercentage = royaltyPercentage.replace(',', '.');
        this.royaltyPercentage = royaltyPercentage;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public LocalDate getContractDate() {
        return contractDate;
    }

    public void setContractDate(LocalDate contractDate) {
        this.contractDate = contractDate;
    }

    public LocalDate getTransferDateOfRIA() {
        return transferDateOfRIA;
    }

    public void setTransferDateOfRIA(LocalDate transferDateOfRIA) {
        this.transferDateOfRIA = transferDateOfRIA;
    }

    public String getK2() {
        return k2;
    }

    public void setK2(String k2) {
        this.k2 = k2;
    }
}
