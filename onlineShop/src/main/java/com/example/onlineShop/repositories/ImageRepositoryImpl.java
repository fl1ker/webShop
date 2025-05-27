package com.example.onlineShop.repositories;

import com.example.onlineShop.models.Image;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ImageRepositoryImpl implements ImageRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Image save(Image image) {
        if (image.getId() == null) {
            entityManager.persist(image);
            return image;
        } else {
            return entityManager.merge(image);
        }
    }

    @Override
    public Optional<Image> findById(Long id) {
        Image image = entityManager.find(Image.class, id);
        return Optional.ofNullable(image);
    }


    @Override
    public void delete(Image image) {
        if (entityManager.contains(image)) {
            entityManager.remove(image);
        } else {
            entityManager.remove(entityManager.merge(image));
        }
    }

}