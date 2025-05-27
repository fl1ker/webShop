package com.example.onlineShop.repositories;

import com.example.onlineShop.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository  {
    CartItem save(CartItem cartItem);
    void delete(CartItem cartItem);
    CartItem findById(Long id);

}