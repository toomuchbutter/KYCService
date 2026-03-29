package com.springBootProject.unifiedBankingSystem.KYC.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.springBootProject.unifiedBankingSystem.KYC.entity.KYCDocument;

public interface KYCDocumentRepository extends JpaRepository<KYCDocument, Long> {
}