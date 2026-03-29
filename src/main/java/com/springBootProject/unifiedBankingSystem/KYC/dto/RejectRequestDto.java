package com.springBootProject.unifiedBankingSystem.KYC.dto;


public class RejectRequestDto {
    private String remarks;
    
    public RejectRequestDto() {}
    public RejectRequestDto(String remarks) { this.remarks = remarks; }
    
    public String remarks() { return this.remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
