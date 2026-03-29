package com.springBootProject.unifiedBankingSystem.KYC.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springBootProject.unifiedBankingSystem.KYC.entity.CustomerKYC;

public interface CustomerKYCRepository extends JpaRepository<CustomerKYC, Long> {

	boolean existsByPan(String pan);

}
