package com.example.onlineShop.repositories;

import com.example.onlineShop.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository {
    Cart save(Cart cart);
    Cart findByUserId(Long userId);
    void delete(Cart cart);
    Cart findById(Long id);
}