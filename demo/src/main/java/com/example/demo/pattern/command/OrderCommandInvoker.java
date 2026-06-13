package com.example.demo.pattern.command;

import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * COMMAND PATTERN – Invoker
 *
 * OrderCommandInvoker là "Invoker" trong Command Pattern.
 * Nó không biết gì về logic bên trong mỗi Command,
 * chỉ chịu trách nhiệm:
 *  1. Gọi execute() để thực thi lệnh.
 *  2. Lưu lịch sử các lệnh đã thực thi.
 *  3. Gọi undo() để hoàn tác lệnh cuối cùng.
 *
 * Được đăng ký là Spring Bean (@Component) để inject vào controller.
 * Lịch sử lệnh (commandHistory) được giữ trong memory của bean.
 */
@Component
public class OrderCommandInvoker {

    // Ngăn xếp lưu lịch sử các lệnh đã thực thi (hỗ trợ undo)
    private final Deque<OrderCommand> commandHistory = new ArrayDeque<>();

    /**
     * Thực thi một lệnh và lưu vào lịch sử để có thể undo.
     *
     * @param command lệnh cần thực thi
     */
    public void executeCommand(OrderCommand command) {
        command.execute();
        commandHistory.push(command);
    }

    /**
     * Hoàn tác lệnh cuối cùng đã thực thi.
     *
     * @return true nếu hoàn tác thành công, false nếu không có lệnh nào trong lịch sử
     */
    public boolean undoLastCommand() {
        if (!commandHistory.isEmpty()) {
            OrderCommand lastCommand = commandHistory.pop();
            lastCommand.undo();
            return true;
        }
        return false;
    }

    /**
     * Kiểm tra xem có thể undo không.
     */
    public boolean canUndo() {
        return !commandHistory.isEmpty();
    }

    /**
     * Xoá toàn bộ lịch sử lệnh.
     */
    public void clearHistory() {
        commandHistory.clear();
    }
}
