package com.springBootProject.unifiedBankingSystem.KYC.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import com.springBootProject.unifiedBankingSystem.KYC.dto.DocumentUploadRequest;
import com.springBootProject.unifiedBankingSystem.KYC.dto.KYCCreationRequestDto;
import com.springBootProject.unifiedBankingSystem.KYC.entity.CustomerKYC;
import com.springBootProject.unifiedBankingSystem.KYC.entity.KYCDocument;
import com.springBootProject.unifiedBankingSystem.KYC.enums.KYCStatus;
import com.springBootProject.unifiedBankingSystem.KYC.exception.DuplicatePanException;
import com.springBootProject.unifiedBankingSystem.KYC.exception.InvalidStateException;
import com.springBootProject.unifiedBankingSystem.KYC.exception.ResourceNotFoundException;
import com.springBootProject.unifiedBankingSystem.KYC.filters.KYCFilters;
import com.springBootProject.unifiedBankingSystem.KYC.repository.CustomerKYCRepository;
import com.springBootProject.unifiedBankingSystem.KYC.repository.KYCDocumentRepository;
import com.springBootProject.unifiedBankingSystem.KYC.utils.ValidationUtil;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import java.security.MessageDigest;

@Service
public class CustomerKYCService {
	
	private CustomerKYCRepository customerRepo;
	private final KYCDocumentRepository documentRepo;
	private KYCFilters filters;
	
	public CustomerKYCService(CustomerKYCRepository customerRepo, KYCDocumentRepository documentRepo, KYCFilters filters) {
		this.documentRepo = documentRepo;
		this.customerRepo=customerRepo;
		this.filters=filters;
	}
	
	
	
	
	
	@PostMapping
	public String createKYC(@RequestBody KYCCreationRequestDto dto) {
		
		if (!ValidationUtil.isValidPan(dto.pan())) {
            throw new RuntimeException("Invalid PAN");
        }

        if (customerRepo.existsByPan(dto.pan())) {
        	throw new RuntimeException("PAN Already Exist");
            //throw new DuplicatePanException("PAN already exists");
        	//needs kyc response status code based reply
        }
		
        else {
        	CustomerKYC kyc = new CustomerKYC();
            kyc.setCustomerId(dto.customerId());
            kyc.setFullName(dto.fullName());
            kyc.setPan(dto.pan());
            kyc.setAadharMasked(ValidationUtil.maskAadhaar(dto.aadhar()));
            kyc.setStatus(KYCStatus.PENDING);
            kyc.setCreatedAt(LocalDateTime.now());
            kyc.setUpdatedAt(LocalDateTime.now());
            kyc.setDeleted(false);

            customerRepo.save(kyc);
            return "KYC Created";
        }
		
	}





	public String uploadDocs(Long id, DocumentUploadRequest request) {
        
        
        if(filters.getKyc(id,customerRepo) != null) {
        	String generatedChecksum = filters.generateMockChecksum(request);
            
            KYCDocument document = new KYCDocument();
            document.setKycId(request.getCustomerId());
            document.setFileName(request.getFileName());
            document.setContentType(request.getFileType());
            document.setChecksum(generatedChecksum);
            document.setUploadedAt(LocalDateTime.now()); 
            documentRepo.save(document);
            
            return "Document Uploaded";
        	
        }
        else{
                throw new ResourceNotFoundException("KYC Profile not found");
        }
            
      }
	
	public List<CustomerKYC> getAllKyc(String status, String pan) {

	    List<CustomerKYC> list = customerRepo.findAll()
	            .stream()
	            .filter(k -> !k.isDeleted())
	            .toList();

	    if (status != null) {
	        list = list.stream()
	                .filter(k -> k.getStatus().name().equalsIgnoreCase(status))
	                .toList();
	    }

	    if (pan != null) {
	        list = list.stream()
	                .filter(k -> k.getPan().equalsIgnoreCase(pan))
	                .toList();
	    }

	    return list;
	}




	public String updateKyc(Long id, KYCCreationRequestDto dto) {

	    CustomerKYC kyc = filters.getKyc(id,customerRepo);

	    if (kyc.getStatus() != KYCStatus.PENDING) {
	        throw new InvalidStateException("Only PENDING KYC can be updated");
	    }

	    if (!kyc.getPan().equals(dto.pan()) && customerRepo.existsByPan(dto.pan())) {
	        throw new DuplicatePanException("PAN already exists");
	    }

	    kyc.setCustomerId(dto.customerId());
	    kyc.setFullName(dto.fullName());
	    kyc.setPan(dto.pan());
	    
//	    String aadhaar = dto.aadhar() != null 
//	            ? dto.aadhar() 
//	            : kyc.getAadharMasked();
//	    
//	    if (aadhaar == null) {
//	    	System.out.println(dto.aadhar());
//	    	System.out.println(kyc.getAadharMasked());
//
//	        throw new IllegalArgumentException("Aadhaar cannot be null");
//	    }
	    
	    
	    //String masked = ValidationUtil.maskAadhaar(dto.aadhar());
	    kyc.setAadharMasked(ValidationUtil.maskAadhaar(dto.aadhar()));
	    //kyc.setAadharMasked(ValidationUtil.maskAadhaar(dto.aadhar()));
	    kyc.setUpdatedAt(LocalDateTime.now());

	    customerRepo.save(kyc);
	    return "KYC Updated";
	}
	
	
	public String verifyKyc(Long id) {
        CustomerKYC kyc = filters.getKyc(id,customerRepo);

        if (kyc.getStatus() != KYCStatus.PENDING) {
            throw new InvalidStateException("Invalid transition");
        }

        kyc.setStatus(KYCStatus.VERIFIED);
        kyc.setUpdatedAt(LocalDateTime.now());
        customerRepo.save(kyc);

        return "KYC Verified";
    }
	
	
	
	public String rejectKyc(Long id, String remarks) {
        CustomerKYC kyc = filters.getKyc(id,customerRepo);

        if (kyc.getStatus() != KYCStatus.PENDING) {
            throw new InvalidStateException("Invalid transition");
        }

        kyc.setStatus(KYCStatus.REJECTED);
        kyc.setRemarks(remarks);
        kyc.setUpdatedAt(LocalDateTime.now());

        customerRepo.save(kyc);
        return "KYC Rejected";
    }
	
	public String deleteKyc(Long id) {
        CustomerKYC kyc = filters.getKyc(id,customerRepo);
        kyc.setDeleted(true);
        customerRepo.save(kyc);
        return "KYC Deleted";
    }
	
	public List<KYCDocument> getDocuments(Long id) {
	    //filters.getKyc(id,customerRepo);
	    return documentRepo.findAll()
	            .stream()
	            .filter(doc -> doc.getKycId().equals(id))
	            .toList();
	}
	
}
