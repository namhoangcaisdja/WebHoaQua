package com.example.demo.pattern.command;

import com.example.demo.entity.OrderStatus;
import com.example.demo.entity.ShopOrder;
import com.example.demo.repository.ShopOrderRepository;

/**
 * COMMAND PATTERN – Concrete Command
 *
 * UpdateOrderStatusCommand đóng gói toàn bộ thông tin cần thiết
 * để thực hiện thao tác "cập nhật trạng thái đơn hàng".
 *
 * Tích hợp với Memento: lưu trạng thái cũ trước khi thay đổi,
 * cho phép undo() khôi phục về trạng thái trước đó.
 *
 * Cách dùng trong OrderCommandInvoker:
 *   OrderCommand cmd = new UpdateOrderStatusCommand(order, PROCESSING, repo);
 *   invoker.executeCommand(cmd); // thực thi
 *   invoker.undoLastCommand();   // hoàn tác
 */
public class UpdateOrderStatusCommand implements OrderCommand {

    private final ShopOrder order;
    private final OrderStatus newStatus;
    private final ShopOrderRepository repository;

    // Memento: lưu trạng thái cũ để có thể undo
    private OrderStatus previousStatus;

    public UpdateOrderStatusCommand(ShopOrder order,
                                    OrderStatus newStatus,
                                    ShopOrderRepository repository) {
        this.order = order;
        this.newStatus = newStatus;
        this.repository = repository;
    }

    /**
     * Thực thi: lưu trạng thái hiện tại vào memento rồi cập nhật sang trạng thái mới.
     */
    @Override
    public void execute() {
        // Lưu memento (snapshot trạng thái hiện tại) trước khi thay đổi
        this.previousStatus = order.getStatus();

        // Thực thi thay đổi
        order.setStatus(newStatus);
        repository.save(order);
    }

    /**
     * Hoàn tác: khôi phục trạng thái cũ đã lưu trong memento.
     */
    @Override
    public void undo() {
        if (previousStatus != null) {
            order.setStatus(previousStatus);
            repository.save(order);
        }
    }

    public OrderStatus getPreviousStatus() {
        return previousStatus;
    }

    public OrderStatus getNewStatus() {
        return newStatus;
    }
}
