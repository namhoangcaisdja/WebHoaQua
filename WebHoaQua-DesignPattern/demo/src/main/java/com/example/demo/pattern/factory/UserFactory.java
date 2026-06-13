package com.example.demo.pattern.factory;

import com.example.demo.pattern.Role;
import com.example.demo.pattern.User;

// Factory Pattern: tạo User theo vai trò, giúp tách logic khởi tạo khỏi code chính
public class UserFactory {
    public static User createUser(String username, String password, Role role) {
        return new User(username, password, role);
    }
}
