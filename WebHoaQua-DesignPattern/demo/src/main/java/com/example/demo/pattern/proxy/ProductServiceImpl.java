package com.example.demo.pattern.proxy;

// Lớp xử lý thật các chức năng sản phẩm
public class ProductServiceImpl implements ProductService {
    @Override
    public void viewProducts() {
        System.out.println("Hien thi danh sach hoa qua");
    }

    @Override
    public void addProduct(String productName) {
        System.out.println("Da them san pham: " + productName);
    }

    @Override
    public void deleteProduct(String productName) {
        System.out.println("Da xoa san pham: " + productName);
    }
}
