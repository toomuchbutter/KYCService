package com.springBootProject.unifiedBankingSystem.KYC.utils;


public class ValidationUtil {

    public static boolean isValidPan(String pan) {
        return pan != null && pan.matches("^[A-Z]{5}[0-9]{4}[A-Z]{1}$");
    }

    public static String maskAadhaar(String aadhaar) {

        if (aadhaar == null) {
            throw new IllegalArgumentException("Aadhaar cannot be null");
        }

        if (aadhaar.length() != 12) {
            throw new IllegalArgumentException("Aadhaar must be 12 digits");
        }

        return "XXXX-XXXX-" + aadhaar.substring(8);
    }
}