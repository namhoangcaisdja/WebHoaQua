package com.example.demo.pattern.proxy;

import com.example.demo.pattern.Role;
import com.example.demo.pattern.singleton.SecurityContext;

// Proxy Pattern: kiểm tra quyền trước khi cho phép thêm/xóa sản phẩm
public class ProductServiceProxy implements ProductService {
    private final ProductService productService = new ProductServiceImpl();

    @Override
    public void viewProducts() {
        productService.viewProducts();
    }

    @Override
    public void addProduct(String productName) {
        if (isAdmin()) {
            productService.addProduct(productName);
        } else {
            System.out.println("Ban khong co quyen them san pham");
        }
    }

    @Override
    public void deleteProduct(String productName) {
        if (isAdmin()) {
            productService.deleteProduct(productName);
        } else {
            System.out.println("Ban khong co quyen xoa san pham");
        }
    }

    private boolean isAdmin() {
        return SecurityContext.getInstance().isLoggedIn()
                && SecurityContext.getInstance().getCurrentUser().getRole() == Role.ADMIN;
    }
}
