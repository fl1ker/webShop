package com.example.onlineShop.repositories;

import com.example.onlineShop.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByTitleContainingAndActiveTrue(String title);
    List<Product> findByActiveTrue();

}
