package com.example.onlineShop.controllers;

import com.example.onlineShop.models.User;
import com.example.onlineShop.models.enums.Role;
import com.example.onlineShop.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Map;

@Controller
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin")
    public String admin(Model model, Principal principal){
        model.addAttribute("users", userService.list());
        model.addAttribute("user", userService.getUserByPrincipal(principal));
        return "admin";
    }

    @PostMapping("/admin/user/ban/{id}")
    public String userBan(@PathVariable("id") Long id){
        userService.banUser(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/user/edit/{id}")
    public String userEdit(@PathVariable("id") Long id, Model model){
        User user = userService.getUserByPrincipal(null); // Заглушка, если нужен текущий пользователь
        User targetUser = userService.getUserById(id); // Предполагается, что добавим метод getUserById
        if (targetUser == null) {
            return "redirect:/admin"; // Если пользователь не найден, перенаправляем
        }
        model.addAttribute("user", targetUser);
        model.addAttribute("roles", Role.values());
        return "user-edit";
    }

    @PostMapping("/admin/user/edit")
    public String userEdit(@RequestParam("userId") Long userId, @RequestParam Map<String, String> form){
        User user = userService.getUserById(userId);
        if (user == null) {
            return "redirect:/admin"; // Если пользователь не найден, перенаправляем
        }
        userService.changeUserRoles(user, form);
        return "redirect:/admin";
    }
}
