package com.example.demo.controller;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dto.CartItemDto;
import com.example.demo.entity.Customer;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.OrderStatus;
import com.example.demo.entity.Product;
import com.example.demo.entity.ShopOrder;
import com.example.demo.entity.UserAccount;
import com.example.demo.pattern.decorator.BaseProductComponent;
import com.example.demo.pattern.decorator.EngravingDecorator;
import com.example.demo.pattern.decorator.PeelingDecorator;
import com.example.demo.pattern.decorator.ProductComponent;
import com.example.demo.pattern.decorator.WrappingDecorator;

import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ShopOrderRepository;
import com.example.demo.repository.UserAccountRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class ShopController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CustomerRepository customerRepository;
    private final ShopOrderRepository shopOrderRepository;
    private final UserAccountRepository userAccountRepository;
    private final com.example.demo.service.discount.DiscountService discountService;

    public ShopController(ProductRepository productRepository, 
                          CategoryRepository categoryRepository,
                          CustomerRepository customerRepository,
                          ShopOrderRepository shopOrderRepository,
                          UserAccountRepository userAccountRepository,
                          com.example.demo.service.discount.DiscountService discountService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.customerRepository = customerRepository;
        this.shopOrderRepository = shopOrderRepository;
        this.userAccountRepository = userAccountRepository;
        this.discountService = discountService;
    }

    @GetMapping({"/", "/shop"})
    public String shop(@RequestParam(required = false) Long categoryId, Model model) {
        List<Product> products = productRepository.findAll();
        if (categoryId != null) {
            products = products.stream()
                    .filter(product -> product.getCategory() != null && categoryId.equals(product.getCategory().getId()))
                    .toList();
        }
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("selectedCategoryId", categoryId);
        return "shop";
    }

    @PostMapping("/shop/cart/add")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> addToCart(
            @RequestParam Long productId,
            @RequestParam(required = false) String[] addons,
            @RequestParam(defaultValue = "1") Integer quantity,
            HttpSession session) {
            
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return ResponseEntity.badRequest().body(Map.of("error", "Không tìm thấy sản phẩm"));

        Map<String, CartItemDto> cart = (Map<String, CartItemDto>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }

        // Apply Decorator Pattern
        ProductComponent component = new BaseProductComponent(product);
        List<String> selectedAddons = addons != null ? Arrays.asList(addons) : new ArrayList<>();
        
        if (selectedAddons.contains("engrave")) {
            component = new EngravingDecorator(component);
        }
        if (selectedAddons.contains("peel")) {
            component = new PeelingDecorator(component);
        }
        if (selectedAddons.contains("wrap")) {
            component = new WrappingDecorator(component);
        }

        // Generate a unique key based on productId and selected addons
        String addonKey = selectedAddons.stream().sorted().collect(Collectors.joining("-"));
        String cartKey = productId + "_" + addonKey;

        if (cart.containsKey(cartKey)) {
            CartItemDto existing = cart.get(cartKey);
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            CartItemDto newItem = new CartItemDto();
            newItem.setCartKey(cartKey);
            newItem.setProductId(productId);
            newItem.setDisplayName(component.getName());
            newItem.setUnitPrice(component.getPrice());
            newItem.setQuantity(quantity);
            newItem.setImage(product.getImage());
            newItem.setAddons(component.getAddons());
            cart.put(cartKey, newItem);
        }

        int totalItems = cart.values().stream().mapToInt(CartItemDto::getQuantity).sum();
        return ResponseEntity.ok(Map.of("success", true, "totalItems", totalItems));
    }

    @PostMapping("/shop/cart/update")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> updateCart(@RequestParam String cartKey, @RequestParam Integer quantity, HttpSession session) {
        Map<String, CartItemDto> cart = (Map<String, CartItemDto>) session.getAttribute("cart");
        if (cart != null && cart.containsKey(cartKey)) {
            if (quantity <= 0) {
                cart.remove(cartKey);
            } else {
                cart.get(cartKey).setQuantity(quantity);
            }
        }
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/shop/cart/remove")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> removeFromCart(@RequestParam String cartKey, HttpSession session) {
        Map<String, CartItemDto> cart = (Map<String, CartItemDto>) session.getAttribute("cart");
        if (cart != null) {
            cart.remove(cartKey);
        }
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/shop/cart/items")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> getCartItems(HttpSession session) {
        Map<String, CartItemDto> cart = (Map<String, CartItemDto>) session.getAttribute("cart");
        List<Map<String, Object>> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        int totalItems = 0;

        if (cart != null && !cart.isEmpty()) {
            // Batch load products to avoid N+1 queries
            List<Long> ids = cart.values().stream().map(CartItemDto::getProductId).distinct().toList();
            List<Product> products = productRepository.findAllById(ids);
            Map<Long, Product> prodMap = products.stream().collect(Collectors.toMap(Product::getId, p -> p));

            for (CartItemDto item : cart.values()) {
                Product product = prodMap.get(item.getProductId());
                if (product != null) {
                    int qty = item.getQuantity();
                    totalItems += qty;
                    BigDecimal subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(qty));
                    total = total.add(subtotal);
                    items.add(Map.of(
                        "cartKey", item.getCartKey(),
                        "name", item.getDisplayName(),
                        "price", item.getUnitPrice(),
                        "image", item.getImage() != null ? (item.getImage().startsWith("/") ? item.getImage() : "/images/" + item.getImage()) : "/images/default.png",
                        "quantity", qty,
                        "subtotal", subtotal,
                        "stock", product.getStock()
                    ));
                }
            }
        }

        java.math.BigDecimal discount = java.math.BigDecimal.ZERO;
        java.math.BigDecimal payable = total;
        try {
            discount = discountService.calculateTotalDiscount(total);
            if (discount == null) discount = java.math.BigDecimal.ZERO;
            payable = total.subtract(discount);
        } catch (Exception ignored) {
        }

        return ResponseEntity.ok(Map.of(
                "items", items,
                "total", total,
                "discount", discount,
                "payable", payable,
                "totalItems", totalItems
        ));
    }

    @PostMapping("/shop/cart/checkout")
    @ResponseBody
    @Transactional
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> checkout(HttpSession session, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Vui lòng đăng nhập trước khi thanh toán."));
        }
        Map<String, CartItemDto> cart = (Map<String, CartItemDto>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Giỏ hàng của bạn đang trống."));
        }
        
        String username = principal.getName();
        UserAccount account = userAccountRepository.findByUsername(username).orElse(null);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Không tìm thấy tài khoản người dùng."));
        }
        
        Customer customer = customerRepository.findByEmail(account.getEmail()).orElse(null);
        if (customer == null) {
            customer = new Customer();
            customer.setName(account.getFullName());
            customer.setEmail(account.getEmail());
            customer.setActive(true);
            customer = customerRepository.save(customer);
        }
        
        ShopOrder order = new ShopOrder();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.NEW);
        order.setCreatedAt(LocalDateTime.now());
        
        // Batch load products for all cart items
        List<Long> ids = cart.values().stream().map(CartItemDto::getProductId).distinct().toList();
        List<Product> products = productRepository.findAllById(ids);
        Map<Long, Product> prodMap = products.stream().collect(Collectors.toMap(Product::getId, p -> p));

        for (CartItemDto itemDto : cart.values()) {
            Product product = prodMap.get(itemDto.getProductId());
            if (product != null) {
                int qtyNeeded = itemDto.getQuantity();
                if (product.getStock() < qtyNeeded) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Sản phẩm '" + product.getName() + "' chỉ còn " + product.getStock() + " sản phẩm trong kho."));
                }

                // Deduct stock
                product.setStock(product.getStock() - qtyNeeded);
                productRepository.save(product);

                OrderItem item = new OrderItem();
                item.setProduct(product);
                item.setQuantity(qtyNeeded);
                item.setUnitPrice(itemDto.getUnitPrice()); // Save the decorated price

                // Save addons as a comma-separated string
                if (itemDto.getAddons() != null && !itemDto.getAddons().isEmpty()) {
                    item.setAddons(String.join(", ", itemDto.getAddons()));
                }

                order.addItem(item);
            }
        }
        
        // apply discount
        order.recalculateTotal();
        java.math.BigDecimal discount = discountService.calculateTotalDiscount(order.getTotalAmount());
        order.setDiscountAmount(discount);
        order.setPayableAmount(order.getTotalAmount().subtract(discount == null ? java.math.BigDecimal.ZERO : discount));

        shopOrderRepository.save(order);
        session.removeAttribute("cart");
        
        return ResponseEntity.ok(Map.of("success", true, "orderId", order.getId()));
    }
}
