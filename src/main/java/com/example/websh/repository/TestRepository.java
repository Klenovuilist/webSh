package com.example.websh.repository;

import com.example.websh.entity.TestEntity;
import com.example.websh.entity.UsersEntity;
import com.example.websh.exceptions.NoValidRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;

@Repository
public class TestRepository {

    private final EntityManager entityManager;

    @Autowired
    public TestRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Сохранить тестовый объект
     */

    public boolean saveUserEntity(UsersEntity testData) {
        try {
            Session session = entityManager.unwrap(Session.class);
            session.persist(testData);
            return true;

        } catch (RuntimeException e) {
            throw new RuntimeException("Ошибка при попытке сохранения testData", e);
        }
    }

    /**
     * Получить тестовый объект
     */

    public TestEntity getTestEntityById(UUID id) {

        Session session = entityManager.unwrap(Session.class);
        return Optional.ofNullable(session.get(TestEntity.class, id)).orElse(null);

    }

    /**
     * Удалить тестовый объект по id
     */
    public boolean deleteDataTestById(UUID id) {
        try {
            Session session = entityManager.unwrap(Session.class);
            TestEntity data = session.get(TestEntity.class, id);
            if (data != null) {
                session.remove(data);
                return true;
            } else {
                return false;
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Ошибка при попытке удаления testData", e);
        }
    }

    public boolean saveTestEntity(TestEntity testEntity) {
        try {
            Session session = entityManager.unwrap(Session.class);
            session.save(testEntity);
            session.flush();
            return true;

        }
        //Ошибка запроса к базе
        catch (ConstraintViolationException e){

            String massege = e.getMessage();
            // Получение подробностей ошибки
            String massegeForFront  = massege.substring(massege.indexOf("Подробности") + 12, massege.indexOf("."));
            System.out.println("----------------------");
            System.out.println(massegeForFront);


            throw new NoValidRequest("Ошибка при попытке сохранения testEntity: " + massegeForFront);
        }
        catch (RuntimeException e) {
            throw new NoValidRequest("Ошибка при попытке сохранения testEntity");
        }
    }

    public void merge(TestEntity testEntity) {
        try {
            Session session = entityManager.unwrap(Session.class);
            session.merge(testEntity);
            session.flush();

        } catch (RuntimeException e) {
            throw new NoValidRequest("Ошибка при попытке coхранения изменений в testEntity");
        }
    }

    public List<TestEntity> findAllTestEntity() {
        Session session = entityManager.unwrap(Session.class);

        return session.createQuery("FROM TestEntity ", TestEntity.class).list();

    }

    public void deleteTestEntityById(UUID uuid) {
        Session session = entityManager.unwrap(Session.class);
        TestEntity deletetestEntity = session.get(TestEntity.class, uuid);

        session.remove(deletetestEntity);
    }
}