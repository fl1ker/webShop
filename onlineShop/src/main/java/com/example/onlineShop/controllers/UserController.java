package com.example.onlineShop.controllers;

import com.example.onlineShop.models.User;
import com.example.onlineShop.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(Principal principal, Model model) {
        model.addAttribute("user", userService.getUserByPrincipal(principal));
        return "login";
    }


    @GetMapping("/profile")
    public String profile(Principal principal,
                          Model model) {
        User user = userService.getUserByPrincipal(principal);
        model.addAttribute("user", user);
        model.addAttribute("orders", user.getOrders());
        return "profile";
    }

    @GetMapping("/registration")
    public String registration(Principal principal, Model model) {
        model.addAttribute("user", userService.getUserByPrincipal(principal));
        return "registration";
    }

    @PostMapping("/registration")
    public String createUser(User user, Model model) {
        if (!userService.createUser(user)) {
            model.addAttribute("errorMessage", "Пользователь с email: " + user.getEmail() + " уже существует");
            return "registration";
        }
        return "redirect:/login";
    }

    @GetMapping("/user/{id}")
    public String userInfo(@PathVariable("id") Long id, Model model, Principal principal) {
        User user = userService.getUserById(id); // Предполагается, что добавим метод getUserById
        if (user == null) {
            return "redirect:/"; // Если пользователь не найден, перенаправляем
        }
        model.addAttribute("user", user);
        model.addAttribute("userByPrincipal", userService.getUserByPrincipal(principal));
        model.addAttribute("products", user.getProducts());
        return "user-info";
    }
}
