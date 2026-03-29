package com.springBootProject.unifiedBankingSystem.KYC.exception;

public class DuplicatePanException extends RuntimeException {
    public DuplicatePanException(String msg) {
        super(msg);
    }
}