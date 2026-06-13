package com.example.demo.pattern;

import com.example.demo.pattern.decorator.BasicFruit;
import com.example.demo.pattern.decorator.Fruit;
import com.example.demo.pattern.decorator.GiftWrapDecorator;
import com.example.demo.pattern.factory.UserFactory;
import com.example.demo.pattern.proxy.ProductService;
import com.example.demo.pattern.proxy.ProductServiceProxy;
import com.example.demo.pattern.singleton.SecurityContext;
import com.example.demo.pattern.strategy.PasswordEncoderStrategy;
import com.example.demo.pattern.strategy.SHA256PasswordEncoder;

public class PatternDemo {
    public static void main(String[] args) {
        PasswordEncoderStrategy encoder = new SHA256PasswordEncoder();

        User customer = UserFactory.createUser("khachhang", encoder.encode("123456"), Role.CUSTOMER);
        User admin = UserFactory.createUser("admin", encoder.encode("admin123"), Role.ADMIN);

        ProductService productService = new ProductServiceProxy();

        System.out.println("===== Customer dang nhap =====");
        SecurityContext.getInstance().login(customer);
        productService.viewProducts();
        productService.addProduct("Tao My");
        productService.deleteProduct("Cam Sanh");

        System.out.println();
        System.out.println("===== Admin dang nhap =====");
        SecurityContext.getInstance().logout();
        SecurityContext.getInstance().login(admin);
        productService.viewProducts();
        productService.addProduct("Cam Sanh");
        productService.deleteProduct("Tao My");

        System.out.println();
        System.out.println("===== Decorator Pattern =====");
        Fruit fruit = new BasicFruit("Gio trai cay", 150000);
        Fruit giftFruit = new GiftWrapDecorator(fruit);
        System.out.println(giftFruit.getDescription());
        System.out.println("Gia sau khi goi qua: " + giftFruit.getPrice());
    }
}
