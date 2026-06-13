package com.example.demo.pattern.prototype;

import com.example.demo.entity.OrderItem;
import com.example.demo.entity.ShopOrder;
import com.example.demo.entity.OrderStatus;

import java.time.LocalDateTime;

/**
 * PROTOTYPE PATTERN
 *
 * OrderPrototype cung cấp hàm clone() để nhân bản (deep copy) một đơn hàng cũ
 * thành một đơn hàng MỚI hoàn toàn độc lập.
 *
 * Tại sao cần Prototype ở đây?
 *  - Một đơn hàng trái cây có thể có nhiều mục (item), mỗi mục lại có giá,
 *    số lượng, dịch vụ đi kèm (addons)... Nếu tạo đơn mới từ đầu sẽ rất tốn công.
 *  - Với Prototype, ta chỉ cần "nhân bản" đơn cũ → chỉnh sửa những trường cần thiết
 *    (ID, ngày tạo, trạng thái) → có ngay một đơn mới sẵn sàng lưu vào DB.
 *
 * Ứng dụng trên UI:
 *  - Nút "🔁 Mua lại" trong trang quản lý đơn hàng.
 *  - Click → hệ thống clone toàn bộ items từ đơn cũ → tạo đơn mới trạng thái NEW.
 */
public class OrderPrototype {

    /**
     * Deep clone một ShopOrder thành một đơn hàng mới.
     *
     * Quy tắc clone:
     *  - ID = null  (để DB tự sinh ID mới khi save)
     *  - createdAt  = thời điểm hiện tại
     *  - status     = NEW (bắt đầu lại từ đầu)
     *  - customer   = giữ nguyên (cùng khách hàng)
     *  - items      = deep copy từng OrderItem (ID = null, order = null)
     *  - totalAmount, discountAmount, payableAmount = giữ nguyên giá trị gốc
     *
     * @param original Đơn hàng gốc cần nhân bản
     * @return Đơn hàng mới (chưa được lưu vào DB)
     */
    public static ShopOrder clone(ShopOrder original) {
        ShopOrder cloned = new ShopOrder();

        // Không copy ID → để JPA tự sinh khi save
        cloned.setCustomer(original.getCustomer());
        cloned.setStatus(OrderStatus.NEW);
        cloned.setCreatedAt(LocalDateTime.now());

        // Deep copy từng item trong đơn hàng
        for (OrderItem originalItem : original.getItems()) {
            OrderItem clonedItem = cloneItem(originalItem);
            cloned.addItem(clonedItem); // addItem() tự gán order và recalculate total
        }

        // Giữ nguyên discount nếu có
        cloned.setDiscountAmount(original.getDiscountAmount());

        return cloned;
    }

    /**
     * Deep clone một OrderItem.
     *
     * @param original Item gốc
     * @return Item mới (ID = null, order = null, sẵn sàng gắn vào đơn mới)
     */
    private static OrderItem cloneItem(OrderItem original) {
        OrderItem cloned = new OrderItem();
        // Không copy ID
        cloned.setProduct(original.getProduct());   // giữ tham chiếu product (không cần copy)
        cloned.setQuantity(original.getQuantity());
        cloned.setUnitPrice(original.getUnitPrice());
        cloned.setAddons(original.getAddons());     // copy chuỗi addons (String là immutable)
        // order sẽ được gán tự động trong ShopOrder.addItem()
        return cloned;
    }
}
