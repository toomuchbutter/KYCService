package com.springBootProject.unifiedBankingSystem.KYC.dto;

import java.time.LocalDateTime;

import com.springBootProject.unifiedBankingSystem.KYC.enums.KYCStatus;

public class KYCCreationRequestDto {
    private long customerId;
    private String fullName;
    private String pan;
    private String aadhar;
    

    public KYCCreationRequestDto() {}

    public long customerId() { return customerId; }
    public String fullName() { return fullName; }
    public String pan() { return pan; }
    public String aadhar() { return aadhar; }

    public void setCustomerId(long customerId) { this.customerId = customerId; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPan(String pan) { this.pan = pan; }
    public void setAadhar(String aadhar) {this.aadhar=aadhar;}
}
