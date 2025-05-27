package com.example.onlineShop.repositories;

import com.example.onlineShop.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository {
    User findByEmail(String email);
    User findById(Long id);
    User save(User user);
    List<User> findAll();
}