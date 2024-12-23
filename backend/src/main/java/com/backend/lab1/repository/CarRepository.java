package com.backend.lab1.repository;

import com.backend.lab1.entity.Car;
import com.backend.lab1.entity.HumanBeing;
import com.backend.lab1.entity.User;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;

import static com.backend.lab1.entity.User.Role.ADMIN;

@Stateless
public class CarRepository {
    @PersistenceContext
    EntityManager entityManager;

    public Car findByName(String name) {
        var query = entityManager.createQuery("select c from Car c WHERE c.name = :name",
        Car.class
        );
        query.setParameter("name", name);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    public void addCar(Car car) {
        entityManager.persist(car);
    }
    public Boolean isExistsCar(String name) {
        return  (findByName(name) != null);
    }

    public int deleteCar(String carName) {
        var query = entityManager.createQuery("delete from Car c WHERE c.name = :name");
        query.setParameter("name", carName);
        return query.executeUpdate();
    }

    public Boolean isOneCar(String carName) {
        var query = entityManager.createQuery(
                "SELECT COUNT(h) FROM HumanBeing h WHERE h.car.name = :carName",
                Long.class
        );
        query.setParameter("carName", carName);

        Long count = query.getSingleResult();
        return count != null && count == 1;
    }

    public Boolean isCarInHumanBeing(String carName) {
        var query = entityManager.createQuery(
                "select count(h) from HumanBeing h where h.car.name = :carName",
                Long.class
        );
        query.setParameter("carName", carName);

        var count = query.getSingleResult();
        return count != null && count > 0;
    }

    public List<Car> getAllCars() {
        return entityManager.createQuery("SELECT c FROM Car c", Car.class).getResultList();
    }
}
