package com.backend.lab1.repository;

import com.backend.lab1.entity.User;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;

import static com.backend.lab1.entity.User.Role;
import static com.backend.lab1.entity.User.Role.ADMIN;

@Stateless
public class UserRepository {

    @PersistenceContext
    EntityManager entityManager;

    public void save(User user) {
        entityManager.persist(user);
    }
    public void update(User user) {
        entityManager.merge(user);
    }

    public User findByLogin(String login) {
        var query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.login = ?1",
                User.class
        );
        query.setParameter(1, login);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    public List<User> findAdmin() {
        var query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.role = :role",
                User.class
        );
        List <User> admins;
        query.setParameter("role", ADMIN);
        try {
            admins = query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
        if (admins.isEmpty()){
            return null;
        }
        return admins;
    }
    public List<User> findIsWaitingAdmin() {
        var query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.isWaitingAdmin = true",
                User.class
        );
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
