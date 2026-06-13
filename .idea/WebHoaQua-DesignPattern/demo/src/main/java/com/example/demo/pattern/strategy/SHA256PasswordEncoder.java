package com.example.demo.pattern.strategy;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class SHA256PasswordEncoder implements PasswordEncoderStrategy {
    @Override
    public String encode(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte b : hash) {
                result.append(String.format("%02x", b));
            }
            return result.toString();
        } catch (Exception e) {
            throw new RuntimeException("Loi ma hoa mat khau", e);
        }
    }
}
