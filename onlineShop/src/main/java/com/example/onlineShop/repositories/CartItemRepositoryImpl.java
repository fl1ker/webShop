package com.example.onlineShop.repositories;

import com.example.onlineShop.models.CartItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;


@Repository
public class CartItemRepositoryImpl implements CartItemRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public CartItem save(CartItem cartItem) {
        if (cartItem.getId() == null) {
            entityManager.persist(cartItem);
            return cartItem;
        } else {
            return entityManager.merge(cartItem);
        }
    }

    @Override
    public void delete(CartItem cartItem) {
        entityManager.remove(entityManager.contains(cartItem) ?
                cartItem : entityManager.merge(cartItem));
    }

    @Override
    public CartItem findById(Long id) {
        return entityManager.find(CartItem.class, id);
    }


}
