package com.example.demo.controller;
import com.example.demo.entity.OrderStatus;
import com.example.demo.entity.ShopOrder;
import com.example.demo.repository.ShopOrderRepository;
import com.example.demo.pattern.command.OrderCommandInvoker;
import com.example.demo.pattern.command.UpdateOrderStatusCommand;
import com.example.demo.pattern.memento.OrderStatusHistory;
import com.example.demo.pattern.memento.OrderStatusMemento;
import com.example.demo.pattern.prototype.OrderPrototype;

import java.security.Principal;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/orders")
public class OrderController {

    private final ShopOrderRepository orderRepository;

    // COMMAND PATTERN: Invoker quản lý việc thực thi và undo các lệnh
    private final OrderCommandInvoker commandInvoker;

    // MEMENTO PATTERN: Caretaker lưu trữ lịch sử trạng thái đơn hàng
    private final OrderStatusHistory orderStatusHistory;

    public OrderController(ShopOrderRepository orderRepository,
                           OrderCommandInvoker commandInvoker,
                           OrderStatusHistory orderStatusHistory) {
        this.orderRepository = orderRepository;
        this.commandInvoker = commandInvoker;
        this.orderStatusHistory = orderStatusHistory;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        model.addAttribute("statuses", OrderStatus.values());
        return "orders";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Optional<ShopOrder> order = orderRepository.findById(id);
        if (order.isEmpty()) {
            return "redirect:/admin/orders";
        }
        model.addAttribute("order", order.get());
        model.addAttribute("statuses", OrderStatus.values());
        return "order-detail";
    }

    /**
     * COMMAND + MEMENTO PATTERN: Cập nhật trạng thái đơn hàng.
     * - Memento lưu snapshot trạng thái cũ trước khi thay đổi.
     * - Command đóng gói hành động cập nhật, có thể undo sau đó.
     */
    @PostMapping("/update-status")
    public String updateStatus(@RequestParam Long orderId,
                               @RequestParam OrderStatus status,
                               Principal principal) {
        Optional<ShopOrder> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            ShopOrder shopOrder = orderOpt.get();

            // MEMENTO PATTERN: Lưu snapshot trạng thái hiện tại trước khi thay đổi
            String changedBy = (principal != null) ? principal.getName() : "admin";
            OrderStatusMemento snapshot = new OrderStatusMemento(
                    shopOrder.getId(), shopOrder.getStatus(), changedBy);
            orderStatusHistory.save(snapshot);

            // COMMAND PATTERN: Tạo và thực thi lệnh cập nhật trạng thái qua Invoker
            UpdateOrderStatusCommand command = new UpdateOrderStatusCommand(
                    shopOrder, status, orderRepository);
            commandInvoker.executeCommand(command);
        }
        return "redirect:/admin/orders";
    }

    /**
     * COMMAND + MEMENTO PATTERN: Hoàn tác thao tác cập nhật trạng thái gần nhất.
     */
    @PostMapping("/undo-status")
    public String undoStatus(@RequestParam Long orderId) {
        boolean undone = commandInvoker.undoLastCommand();
        if (!undone) {
            // Fallback: khôi phục từ Memento nếu Invoker không có lịch sử
            OrderStatusMemento snapshot = orderStatusHistory.popLast(orderId);
            if (snapshot != null) {
                orderRepository.findById(orderId).ifPresent(order -> {
                    order.setStatus(snapshot.getStatus());
                    orderRepository.save(order);
                });
            }
        }
        return "redirect:/admin/orders/" + orderId;
    }

    /**
     * PROTOTYPE PATTERN: Nhân bản (deep clone) một đơn hàng cũ thành đơn hàng mới.
     *
     * Quy trình:
     *  1. Tìm đơn hàng gốc theo ID.
     *  2. Gọi OrderPrototype.clone() để tạo bản sao sâu (deep copy).
     *  3. Lưu đơn mới vào DB với ID mới, ngày tạo = hôm nay, status = NEW.
     *  4. Chuyển hướng sang trang chi tiết đơn mới với flag ?cloned=true.
     */
    @PostMapping("/clone")
    public String cloneOrder(@RequestParam Long orderId) {
        Optional<ShopOrder> originalOpt = orderRepository.findById(orderId);
        if (originalOpt.isEmpty()) {
            return "redirect:/admin/orders";
        }

        // PROTOTYPE PATTERN: Gọi clone() tạo đơn hàng mới từ bản gốc
        ShopOrder cloned = OrderPrototype.clone(originalOpt.get());
        ShopOrder saved = orderRepository.save(cloned);

        return "redirect:/admin/orders/" + saved.getId() + "?cloned=true";
    }

    /**
     * Xóa đơn hàng khỏi hệ thống.
     */
    @PostMapping("/delete")
    public String deleteOrder(@RequestParam Long orderId) {
        orderRepository.deleteById(orderId);
        return "redirect:/admin/orders";
    }
}
