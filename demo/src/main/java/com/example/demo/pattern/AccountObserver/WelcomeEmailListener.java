package com.example.demo.pattern.AccountObserver;

import com.example.demo.entity.UserAccount;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class WelcomeEmailListener {

    // Inject công cụ gửi mail thật của Spring Boot
    private final JavaMailSender mailSender;

    public WelcomeEmailListener(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async // Vẫn giữ Async để việc gửi mail thật (tốn 1-2 giây) không làm nghẽn luồng đăng ký của khách
    @EventListener
    public void onAccountCreated(AccountCreatedEvent event) {
        UserAccount account = event.account();

        try {
            // Khởi tạo cấu trúc một bức thư thuần văn bản
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("email_cua_cua_hang_ban_hoa_qua@gmail.com");
            message.setTo(account.getEmail()); // Gửi tới email của khách hàng vừa đăng ký
            message.setSubject("Chào mừng bạn đến với Cửa Hàng Trái Cây FruitShop!");
            message.setText("Xin chào " + account.getFullName() + ",\n\n" +
                    "Tài khoản của bạn đã được khởi tạo thành công.\n" +
                    "Tên đăng nhập: " + account.getUsername() + "\n" +
                    "Cảm ơn bạn đã tham gia mua sắm cùng FruitShop!");

            // THỰC HIỆN GỬI THỰC TẾ
            mailSender.send(message);
            System.out.println("[SUCCESS] Email thật đã được bay tới hòm thư: " + account.getEmail());

        } catch (Exception e) {
            System.err.println("[ERROR] Lỗi gửi email thực tế: " + e.getMessage());
        }
    }
}