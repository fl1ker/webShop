package com.example.onlineShop.repositories;

import com.example.onlineShop.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> findByTitleContainingAndActiveTrue(String title);
    List<Product> findByActiveTrue();
    Product save(Product product);
    Optional<Product> findById(Long id);
    void delete(Product product);
}
