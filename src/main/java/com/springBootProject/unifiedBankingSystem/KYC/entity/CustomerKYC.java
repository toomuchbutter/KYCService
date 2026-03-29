package com.springBootProject.unifiedBankingSystem.KYC.entity;

import java.time.LocalDateTime;

import com.springBootProject.unifiedBankingSystem.KYC.enums.KYCStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name="customerKYC", indexes = {
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_pan", columnList = "pan"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
public class CustomerKYC {
	
	//id, customerId, fullName, pan, aadhaarMasked, status, remarks, createdAt, updatedAt, deleted
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	
	private long customerId;
	private String fullName;
	private String pan;
	private String aadhar;
	private KYCStatus status;
	private String remarks;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private boolean deleted;
	
	public CustomerKYC() {}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getPan() {
		return pan;
	}
	public void setPan(String pan) {
		this.pan = pan;
	}
	public String getAadharMasked() {
		return aadhar;
	}
	public void setAadharMasked(String aadhar) {
		this.aadhar = aadhar;
	}
	public KYCStatus getStatus() {
		return status;
	}
	public void setStatus(KYCStatus status) {
		this.status = status;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
}
