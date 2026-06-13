package com.example.demo.pattern.command;

/**
 * COMMAND PATTERN – Command Interface
 *
 * Định nghĩa interface chung cho tất cả các thao tác xử lý đơn hàng.
 * Mỗi thao tác (cập nhật trạng thái, huỷ đơn...) được đóng gói thành
 * một object Command riêng biệt, tách biệt logic ra khỏi controller.
 *
 * Lợi ích:
 *  - Dễ mở rộng thêm thao tác mới mà không sửa code cũ.
 *  - Hỗ trợ undo/redo khi kết hợp với Memento Pattern.
 *  - Controller chỉ cần gọi execute() mà không cần biết bên trong làm gì.
 */
public interface OrderCommand {
    /**
     * Thực thi thao tác xử lý đơn hàng.
     */
    void execute();

    /**
     * Hoàn tác thao tác (khôi phục trạng thái trước đó).
     */
    void undo();
}
