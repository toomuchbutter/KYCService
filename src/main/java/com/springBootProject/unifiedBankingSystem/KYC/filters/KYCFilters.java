package com.springBootProject.unifiedBankingSystem.KYC.filters;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Component;

import com.springBootProject.unifiedBankingSystem.KYC.dto.DocumentUploadRequest;
import com.springBootProject.unifiedBankingSystem.KYC.entity.CustomerKYC;
import com.springBootProject.unifiedBankingSystem.KYC.exception.ResourceNotFoundException;
import com.springBootProject.unifiedBankingSystem.KYC.repository.CustomerKYCRepository;


@Component
public class KYCFilters {
	public CustomerKYC getKyc(Long id, CustomerKYCRepository kycRepo) {
        return kycRepo.findById(id)
                .filter(k -> !k.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("KYC not found"));
    }
	
	public String generateMockChecksum(DocumentUploadRequest request) {
        
        try {
            String dataToHash = request.getCustomerId() + request.getFileName() + 
                                request.getFileType() + System.currentTimeMillis();
            
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating checksum", e);
        }
    }
}
