package com.example.onlineShop.repositories;

import com.example.onlineShop.models.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ProductRepositoryImpl implements ProductRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Product> findByTitleContainingAndActiveTrue(String title) {
        return entityManager.createQuery(
                        "SELECT p FROM Product p WHERE p.title LIKE :title AND p.active = true",
                        Product.class)
                .setParameter("title", "%" + title + "%")
                .getResultList();
    }

    @Override
    public List<Product> findByActiveTrue() {
        return entityManager.createQuery(
                        "SELECT p FROM Product p WHERE p.active = true",
                        Product.class)
                .getResultList();
    }

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            entityManager.persist(product);
            return product;
        } else {
            return entityManager.merge(product);
        }
    }

    @Override
    public Optional<Product> findById(Long id) {
        Product product = entityManager.find(Product.class, id);
        return Optional.ofNullable(product);
    }

    @Override
    public void delete(Product product) {
        entityManager.remove(entityManager.contains(product) ?
                product : entityManager.merge(product));
    }
}
