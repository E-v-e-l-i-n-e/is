package com.backend.lab1.utils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncoding {
    public static String encodePassword(String password){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-384");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SHA-384 algorithm not found.");
        }
        return null;
    }
}