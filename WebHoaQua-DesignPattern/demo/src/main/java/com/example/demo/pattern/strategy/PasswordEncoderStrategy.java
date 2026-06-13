package com.example.demo.pattern.strategy;

// Strategy Pattern: cho phép thay đổi thuật toán mã hóa mật khẩu linh hoạt
public interface PasswordEncoderStrategy {
    String encode(String rawPassword);
}
