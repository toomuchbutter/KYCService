package com.springBootProject.unifiedBankingSystem.KYC.dto;

public class DocumentUploadRequest {
	private Long kycId;
    private String fileName; 
    private String fileType;
	public Long getCustomerId() {
		return kycId;
	}
	public void setCustomerId(Long kycId) {
		this.kycId = kycId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
    
}
