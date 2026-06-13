package com.example.demo.pattern.strategy;

public class PlainPasswordEncoder implements PasswordEncoderStrategy {
    @Override
    public String encode(String rawPassword) {
        return rawPassword;
    }
}
