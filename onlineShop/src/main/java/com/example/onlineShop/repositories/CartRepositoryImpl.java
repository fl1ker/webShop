package com.example.onlineShop.repositories;

import com.example.onlineShop.models.Cart;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

@Repository
public class CartRepositoryImpl implements CartRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Cart save(Cart cart) {
        if (cart.getId() == null) {
            entityManager.persist(cart);
            return cart;
        } else {
            return entityManager.merge(cart);
        }
    }

    @Override
    public Cart findByUserId(Long userId) {
        TypedQuery<Cart> query = entityManager.createQuery(
                "SELECT c FROM Cart c WHERE c.user.id = :userId", Cart.class);
        query.setParameter("userId", userId);
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void delete(Cart cart) {
        entityManager.remove(entityManager.contains(cart) ? cart : entityManager.merge(cart));
    }

    @Override
    public Cart findById(Long id) {
        return entityManager.find(Cart.class, id);
    }
}
