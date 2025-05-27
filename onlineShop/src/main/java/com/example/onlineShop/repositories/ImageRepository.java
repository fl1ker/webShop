package com.example.onlineShop.repositories;

import com.example.onlineShop.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository {
    Image save(Image image);
    Optional<Image> findById(Long id);
    void delete(Image image);
}
