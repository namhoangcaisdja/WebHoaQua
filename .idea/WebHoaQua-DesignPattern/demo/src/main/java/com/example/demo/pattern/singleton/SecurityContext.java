package com.example.demo.pattern.singleton;

import com.example.demo.pattern.User;

// Singleton Pattern: chỉ tạo một đối tượng quản lý phiên đăng nhập trong toàn hệ thống
public class SecurityContext {
    private static SecurityContext instance;
    private User currentUser;

    private SecurityContext() {}

    public static SecurityContext getInstance() {
        if (instance == null) {
            instance = new SecurityContext();
        }
        return instance;
    }

    public void login(User user) {
        this.currentUser = user;
        System.out.println("Dang nhap thanh cong: " + user.getUsername());
    }

    public void logout() {
        this.currentUser = null;
        System.out.println("Da dang xuat");
    }

    public User getCurrentUser() { return currentUser; }
    public boolean isLoggedIn() { return currentUser != null; }
}
