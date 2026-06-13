package com.example.demo.pattern.memento;

import com.example.demo.entity.OrderStatus;
import java.time.LocalDateTime;

/**
 * MEMENTO PATTERN – Memento (Snapshot)
 *
 * OrderStatusMemento lưu trữ một "bản chụp" (snapshot) trạng thái
 * của đơn hàng tại một thời điểm cụ thể.
 *
 * Originator (ShopOrder) tạo ra Memento này để lưu trạng thái,
 * và có thể khôi phục lại từ Memento khi cần undo.
 *
 * Caretaker (OrderStatusHistory) giữ danh sách các Memento.
 *
 * Lợi ích:
 *  - Tách biệt logic lưu trạng thái khỏi business logic.
 *  - Cho phép xem lại lịch sử thay đổi trạng thái đơn hàng.
 *  - Hỗ trợ undo khi kết hợp với Command Pattern.
 */
public class OrderStatusMemento {

    private final Long orderId;
    private final OrderStatus status;
    private final LocalDateTime savedAt;
    private final String changedBy;

    public OrderStatusMemento(Long orderId, OrderStatus status, String changedBy) {
        this.orderId = orderId;
        this.status = status;
        this.savedAt = LocalDateTime.now();
        this.changedBy = changedBy;
    }

    public Long getOrderId() {
        return orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getSavedAt() {
        return savedAt;
    }

    public String getChangedBy() {
        return changedBy;
    }

    @Override
    public String toString() {
        return String.format("OrderStatusMemento[orderId=%d, status=%s, changedBy=%s, savedAt=%s]",
                orderId, status, changedBy,
                savedAt.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
    }
}
