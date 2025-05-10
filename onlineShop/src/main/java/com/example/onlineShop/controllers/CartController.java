package com.example.onlineShop.controllers;

import com.example.onlineShop.models.Cart;
import com.example.onlineShop.services.CartService;
import com.example.onlineShop.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class CartController {
    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping("/cart")
    public String viewCart(Principal principal, Model model) {
        if (principal == null) {
            model.addAttribute("error", "Войдите, чтобы просмотреть корзину.");
            return "login";
        }
        Cart cart = cartService.getCartByPrincipal(principal);
        model.addAttribute("cart", cart);
        model.addAttribute("totalPrice", cartService.getTotalPrice(cart));
        model.addAttribute("user", userService.getUserByPrincipal(principal));
        return "cart";
    }

    @PostMapping("/cart/add/{productId}")
    public String addToCart(@PathVariable Long productId, @RequestParam("quantity") int quantity,
                            Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        cartService.addToCart(principal, productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove/{cartItemId}")
    public String removeFromCart(@PathVariable Long cartItemId, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        cartService.removeFromCart(principal, cartItemId);
        return "redirect:/cart";
    }
}