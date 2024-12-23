package com.backend.lab1.repository;

import com.backend.lab1.dto.FiltersRequest;
import com.backend.lab1.entity.HumanBeing;
import com.backend.lab1.entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class HumanBeingRepository {
    @PersistenceContext
    EntityManager entityManager;

    public void save(HumanBeing humanBeing ) {
        entityManager.persist(humanBeing);
    }

    public void update(HumanBeing humanBeing) {
        entityManager.merge(humanBeing);
    }
    public HumanBeing findById(Long id) {
        var query = entityManager.createQuery(
                "SELECT h FROM HumanBeing h WHERE h.id = :id",
                HumanBeing.class
        );
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    public int deleteHumanBeing(Long id) {
        var query = entityManager.createQuery(
                "delete FROM HumanBeing h WHERE h.id = :id"
        );
        query.setParameter("id", id);
        return query.executeUpdate();
    }

    public List<HumanBeing> findPaginated(int page, int size) {
        return entityManager.createQuery("SELECT h FROM HumanBeing h", HumanBeing.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<HumanBeing> findFilteredPaginated(FiltersRequest request) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<HumanBeing> cq = cb.createQuery(HumanBeing.class);
        Root<HumanBeing> root = cq.from(HumanBeing.class);

        List<Predicate> predicates = new ArrayList<>();

        if (request.getName() != null && !request.getName().isEmpty()) {
            String namePrefix = request.getName();
            String sql = "SELECT * FROM filter_by_name(CAST(:namePrefix AS TEXT))";

            TypedQuery<HumanBeing> query = (TypedQuery<HumanBeing>) entityManager.createNativeQuery(sql, HumanBeing.class);
            query.setParameter("namePrefix", namePrefix);

            List<HumanBeing> nameFilteredResults = query.getResultList();

            if (!nameFilteredResults.isEmpty()) {
                predicates.add(root.in(nameFilteredResults));
            }
        }

        if (request.getCarName() != null && !request.getCarName().isEmpty()) {
            predicates.add(cb.like(root.get("car").get("name"), request.getCarName() + "%")); // Фильтр по имени машины
        }

        if (request.getMood() != null && !request.getMood().isEmpty()) {
            predicates.add(cb.equal(root.get("mood"), request.getMood()));
        }

        if (request.getWeaponType() != null && !request.getWeaponType().isEmpty()) {
            predicates.add(cb.equal(root.get("weaponType"), request.getWeaponType()));
        }

        cq.select(root).where(predicates.toArray(new Predicate[0]));

        int pageSize = request.getSize();
        int pageNumber = request.getPage();
        List<HumanBeing> resultList = entityManager.createQuery(cq)
                .setFirstResult(pageNumber * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        return resultList;
    }

    public int count() {
        return ((Long) entityManager.createQuery("SELECT COUNT(h) FROM HumanBeing h").getSingleResult()).intValue();
    }

    public Double averageImpactSpeed() {
        return (Double) entityManager.createNativeQuery("select average_impact_speed ()").getSingleResult();
    }

    public Integer countWaitingMinutesLessThan(Integer inputMinutes) {
        return (Integer) entityManager.createNativeQuery(
                "select count_waiting_minutes_less_than(:inputMinutes)",
                Integer.class
                )
                .setParameter("inputMinutes", inputMinutes)
                .getSingleResult();
    }

    public void updateCarName() {
            var query = entityManager.createNativeQuery("SELECT update_car_name()");
            query.getSingleResult();
    }

    public void delteNonToothpick() {
        entityManager.createNativeQuery("SELECT delete_non_tothpick()").getSingleResult();

    }

    public List<HumanBeing> getByPrefix(String prefix) {
        String sql = "SELECT * FROM filter_by_name(:namePrefix)";
        return entityManager.createNativeQuery(sql, HumanBeing.class)
                .setParameter("namePrefix", prefix)
                .getResultList();
    }
}
