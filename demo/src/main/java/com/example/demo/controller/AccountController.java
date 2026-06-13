package com.example.demo.controller;

import com.example.demo.entity.AccountRole;
import com.example.demo.entity.UserAccount;
import com.example.demo.pattern.memento.AdminAccountManagementService;
import com.example.demo.service.AccountService;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/accounts")
public class AccountController {

    private final AccountService accountService;
    private final AdminAccountManagementService adminAccountManagementService;

    public AccountController(AccountService accountService,
                             AdminAccountManagementService adminAccountManagementService) {
        this.accountService = accountService;
        this.adminAccountManagementService = adminAccountManagementService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("accounts", accountService.findAll());
        return "accounts";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("account", new UserAccount());
        model.addAttribute("roles", AccountRole.values());
        return "account-form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<UserAccount> account = accountService.findById(id);
        if (account.isEmpty()) {
            return "redirect:/admin/accounts";
        }
        model.addAttribute("account", account.get());
        model.addAttribute("roles", AccountRole.values());
        return "account-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute UserAccount account, RedirectAttributes redirectAttributes) {
        // Nếu là cập nhật tài khoản đã có (có id) → dùng AdminAccountManagementService
        // để snapshot được lưu tự động trước khi ghi đè (Memento Pattern)
        if (account.getId() != null) {
            adminAccountManagementService.updateAccount(
                    account.getId(),
                    account.getFullName(),
                    account.getEmail(),
                    account.getRole(),
                    account.isActive()
            );
            // Xử lý mật khẩu riêng nếu admin có nhập mới
            if (account.getPassword() != null && !account.getPassword().isBlank()) {
                accountService.save(account);
            }
        } else {
            // Tạo mới → dùng AccountService như cũ
            accountService.save(account);
        }
        redirectAttributes.addFlashAttribute("successMessage", "Lưu tài khoản thành công!");
        return "redirect:/admin/accounts";
    }

    @GetMapping("/undo/{id}")
    public String undo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminAccountManagementService.undo(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Hoàn tác thành công! Tài khoản đã được khôi phục về trạng thái trước đó.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không thể hoàn tác: " + e.getMessage());
        }
        return "redirect:/admin/accounts";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        accountService.deleteById(id);
        return "redirect:/admin/accounts";
    }
}