package com.example.onlineShop.repositories;

import com.example.onlineShop.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User findByEmail(String email) {
        try {
            log.info("Finding user by email: {}", email);
            return entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            log.warn("User not found with email: {}", email);
            return null;
        }
    }

    @Override
    public User findById(Long id) {
        log.info("Finding user by id: {}", id);
        return entityManager.find(User.class, id);
    }

    @Override
    public User save(User user) {
        log.info("Saving user with email: {}", user.getEmail());
        if (user.getId() == null) {
            entityManager.persist(user);
        } else {
            user = entityManager.merge(user);
        }
        return user;
    }
    @Override
    public List<User> findAll() {
        log.info("Finding all users");
        return entityManager.createQuery("SELECT u FROM User u", User.class)
                .getResultList();
    }
}