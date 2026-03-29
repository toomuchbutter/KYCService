package com.springBootProject.unifiedBankingSystem.KYC.controller;

import org.springframework.web.bind.annotation.*;

import com.springBootProject.unifiedBankingSystem.KYC.dto.DocumentUploadRequest;
import com.springBootProject.unifiedBankingSystem.KYC.dto.KYCCreationRequestDto;
import org.springframework.web.multipart.MultipartFile;
import com.springBootProject.unifiedBankingSystem.KYC.dto.RejectRequestDto;
import com.springBootProject.unifiedBankingSystem.KYC.entity.CustomerKYC;
import com.springBootProject.unifiedBankingSystem.KYC.entity.KYCDocument;
import com.springBootProject.unifiedBankingSystem.KYC.service.CustomerKYCService;
import java.util.List;

@RestController
@RequestMapping("/KYC")
public class CustomerKYCController {

    private final CustomerKYCService service;

    
//    {
//    	  "customerId": "C123",
//    	  "fullName": "John Doe",
//    	  "pan": "ABCDE1234F",
//    	  "aadhaar": "123456789012"
//    	}
    public CustomerKYCController(CustomerKYCService service) {
        this.service = service;
    }

    @PostMapping
    public String create(@RequestBody KYCCreationRequestDto dto) {
        //System.out.println("API HIT");
        //System.out.println(dto);
        return service.createKYC(dto);
    }
    
    
 // LIST WITH FILTERS
    @GetMapping
    public List<CustomerKYC> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String pan
    ) {
        return service.getAllKyc(status, pan);
    }

    // UPDATE
    @PutMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestBody KYCCreationRequestDto dto) {
        return service.updateKyc(id, dto);
    }

    // SOFT DELETE
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        return service.deleteKyc(id);
    }

    // VERIFY
    @PostMapping("/{id}/verify")
    public String verify(@PathVariable Long id) {
        return service.verifyKyc(id);
    }

    // REJECT
    @PostMapping("/{id}/reject")
    public String reject(@PathVariable Long id,
                         @RequestBody RejectRequestDto dto) {
        return service.rejectKyc(id, dto.remarks());
    }
    
    @PostMapping("/{id}/Documents")

    public String uploadDocument(@PathVariable Long id, 
                                 @RequestBody  DocumentUploadRequest request) {
    	return service.uploadDocs(id, request);
    }
    
    @GetMapping("/{id}/Documents")
    public List<KYCDocument> getDocuments(@PathVariable Long id) {
        return service.getDocuments(id);
    }
    
}