package com.example.demo.pattern.memento;

import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MEMENTO PATTERN – Caretaker
 *
 * OrderStatusHistory là "Caretaker" trong Memento Pattern.
 * Nó không biết gì về nội dung bên trong Memento,
 * chỉ chịu trách nhiệm giữ gìn và quản lý các snapshot.
 *
 * Chức năng:
 *  - Lưu snapshot mới mỗi khi trạng thái đơn hàng thay đổi.
 *  - Cung cấp snapshot cuối cùng để phục vụ undo.
 *  - Cung cấp toàn bộ lịch sử thay đổi của một đơn hàng.
 *
 * Được đăng ký là Spring Bean (@Component) — singleton trong context Spring.
 * Lịch sử được lưu trong memory (Map theo orderId).
 */
@Component
public class OrderStatusHistory {

    // Key: orderId → Value: ngăn xếp các snapshot (mới nhất ở trên cùng)
    private final Map<Long, Deque<OrderStatusMemento>> historyMap = new HashMap<>();

    /**
     * Lưu một snapshot mới cho đơn hàng.
     *
     * @param memento snapshot trạng thái cần lưu
     */
    public void save(OrderStatusMemento memento) {
        historyMap
            .computeIfAbsent(memento.getOrderId(), id -> new ArrayDeque<>())
            .push(memento);
    }

    /**
     * Lấy snapshot gần nhất của đơn hàng (để undo).
     *
     * @param orderId ID đơn hàng
     * @return snapshot gần nhất, hoặc null nếu không có lịch sử
     */
    public OrderStatusMemento popLast(Long orderId) {
        Deque<OrderStatusMemento> history = historyMap.get(orderId);
        if (history != null && !history.isEmpty()) {
            return history.pop();
        }
        return null;
    }

    /**
     * Xem snapshot gần nhất mà không xoá khỏi lịch sử.
     *
     * @param orderId ID đơn hàng
     * @return snapshot gần nhất, hoặc null nếu không có
     */
    public OrderStatusMemento peekLast(Long orderId) {
        Deque<OrderStatusMemento> history = historyMap.get(orderId);
        if (history != null && !history.isEmpty()) {
            return history.peek();
        }
        return null;
    }

    /**
     * Lấy toàn bộ lịch sử thay đổi trạng thái của một đơn hàng
     * (sắp xếp từ cũ đến mới).
     *
     * @param orderId ID đơn hàng
     * @return danh sách các snapshot theo thứ tự thời gian
     */
    public List<OrderStatusMemento> getHistory(Long orderId) {
        Deque<OrderStatusMemento> history = historyMap.get(orderId);
        if (history == null) return new ArrayList<>();
        // Deque có thứ tự mới nhất ở đầu, reverse lại để hiển thị từ cũ đến mới
        List<OrderStatusMemento> result = new ArrayList<>(history);
        java.util.Collections.reverse(result);
        return result;
    }

    /**
     * Kiểm tra xem đơn hàng có lịch sử để undo không.
     */
    public boolean hasHistory(Long orderId) {
        Deque<OrderStatusMemento> history = historyMap.get(orderId);
        return history != null && !history.isEmpty();
    }
}
