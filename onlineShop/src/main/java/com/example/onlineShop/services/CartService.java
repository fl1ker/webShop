package com.example.onlineShop.services;

import com.example.onlineShop.models.Cart;
import com.example.onlineShop.models.Order;
import com.example.onlineShop.models.CartItem;
import com.example.onlineShop.models.Product;
import com.example.onlineShop.models.User;
import com.example.onlineShop.repositories.CartItemRepository;
import com.example.onlineShop.repositories.CartRepository;
import com.example.onlineShop.repositories.ProductRepository;
import com.example.onlineShop.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private static final Logger log = LoggerFactory.getLogger(CartService.class);

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                       ProductRepository productRepository, UserRepository userRepository, EmailService emailService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void addToCart(Principal principal, Long productId, int quantity) {
        User user = userRepository.findByEmail(principal.getName());
        if (user == null) {
            log.error("User not found for principal: {}", principal.getName());
            return;
        }


        Cart cart = cartRepository.findByUserId(user.getId());
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
        }

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            log.error("Product not found with id: {}", productId);
            return;
        }
        if (!product.isActive()) {
            log.error("Cannot add inactive product to cart: {}", productId);
            return;
        }

        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cart.addItem(cartItem);
            cartItemRepository.save(cartItem);
        }

        cartRepository.save(cart);
        log.info("Added product {} to cart for user {}", productId, user.getEmail());
    }

    @Transactional
    public void removeFromCart(Principal principal, Long cartItemId) {
        User user = userRepository.findByEmail(principal.getName());
        if (user == null) {
            log.error("User not found for principal: {}", principal.getName());
            return;
        }

        Cart cart = cartRepository.findByUserId(user.getId());
        if (cart == null) {
            log.error("Cart not found for user: {}", user.getEmail());
            return;
        }

        CartItem item = cartItemRepository.findById(cartItemId);
        if (item != null && item.getCart().getId().equals(cart.getId())) {
            cart.removeItem(item);
            cartItemRepository.delete(item);
            cartRepository.save(cart);
            log.info("Removed item {} from cart for user {}", cartItemId, user.getEmail());
        } else {
            log.error("Cart item {} not found or does not belong to user {}", cartItemId, user.getEmail());
        }
    }

    public Cart getCartByPrincipal(Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        if (user == null) {
            return null;
        }
        return cartRepository.findByUserId(user.getId());
    }

    public int getTotalPrice(Cart cart) {
        if (cart == null || cart.getItems() == null) {
            return 0;
        }
        return cart.getItems().stream()
                .mapToInt(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }

    @Transactional
    public void checkoutCart(Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        Cart cart = cartRepository.findByUserId(user.getId());
        if (cart == null || cart.getItems().isEmpty()) {
            return;
        }

        for (CartItem item : cart.getItems()) {
            Order order = new Order();
            order.setUser(user);
            order.setProduct(item.getProduct());
            order.setQuantity(item.getQuantity());
            order.setPurchaseDate(LocalDateTime.now());
            user.getOrders().add(order); // чтобы сохранить с каскадом, если так настроено


            // Отправка письма
            try {
                emailService.sendPurchaseConfirmation(
                        user.getEmail(),
                        item.getProduct().getTitle(),
                        item.getQuantity()
                );
            } catch (RuntimeException e) {
                log.error("Błąd podczas wysyłania emaila potwierdzającego: {}", e.getMessage());
            }
        }

        cart.getItems().clear(); // очищаем корзину
        cartRepository.save(cart);
        userRepository.save(user); // сохраняем заказы пользователя
    }
}