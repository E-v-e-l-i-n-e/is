package com.backend.lab1.repository;

import com.backend.lab1.entity.Car;
import com.backend.lab1.entity.HistoryChange;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class HistoryChangeRepository {
    @PersistenceContext
    EntityManager entityManager;

    public void addChange(HistoryChange change) {
        entityManager.persist(change);
    }

    public void deleteChange(Long humanBeingId) {
        var query = entityManager.createQuery("delete from HistoryChange h WHERE h.humanBeingId = :humanBeingId");
        query.setParameter("humanBeingId", humanBeingId);
        query.executeUpdate();
    }
}
