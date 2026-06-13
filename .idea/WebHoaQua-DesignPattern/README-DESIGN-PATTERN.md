# Design Pattern áp dụng cho WebHoaQua

Đề tài: **Ứng dụng Design Pattern trong website bán hoa quả bằng Java**.

Project minh họa 5 Design Pattern:

## 1. Singleton Pattern
File: `pattern/singleton/SecurityContext.java`

Dùng để quản lý trạng thái người dùng đang đăng nhập trong toàn hệ thống.

## 2. Factory Pattern
File: `pattern/factory/UserFactory.java`

Dùng để tạo đối tượng người dùng theo vai trò `ADMIN` hoặc `CUSTOMER`.

## 3. Strategy Pattern
File: `pattern/strategy/PasswordEncoderStrategy.java`
File triển khai: `SHA256PasswordEncoder.java`, `PlainPasswordEncoder.java`

Dùng để thay đổi thuật toán mã hóa mật khẩu mà không ảnh hưởng đến code chính.

## 4. Proxy Pattern
File: `pattern/proxy/ProductServiceProxy.java`

Dùng để kiểm tra quyền truy cập trước khi cho phép thêm hoặc xóa sản phẩm.
Customer chỉ được xem sản phẩm, Admin được thêm/xóa sản phẩm.

## 5. Decorator Pattern
File: `pattern/decorator/GiftWrapDecorator.java`

Dùng để bổ sung chức năng gói quà cho sản phẩm hoa quả mà không sửa lớp sản phẩm gốc.

## Cách chạy demo

Mở file:

```txt
demo/src/main/java/com/example/demo/pattern/PatternDemo.java
```

Chạy hàm `main`.

Kết quả mẫu:

```txt
===== Customer dang nhap =====
Dang nhap thanh cong: khachhang
Hien thi danh sach hoa qua
Ban khong co quyen them san pham
Ban khong co quyen xoa san pham

===== Admin dang nhap =====
Da dang xuat
Dang nhap thanh cong: admin
Hien thi danh sach hoa qua
Da them san pham: Cam Sanh
Da xoa san pham: Tao My

===== Decorator Pattern =====
Gio trai cay + goi qua
Gia sau khi goi qua: 160000.0
```

## Kết luận

Việc áp dụng Design Pattern giúp source code dễ bảo trì, dễ mở rộng và thể hiện rõ tư duy thiết kế phần mềm trong chức năng đăng nhập, phân quyền, bảo mật và quản lý sản phẩm của website bán hoa quả.
