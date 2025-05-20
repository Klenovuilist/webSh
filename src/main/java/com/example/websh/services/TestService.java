package com.example.websh.services;


import com.example.websh.entity.TestEntity;
import com.example.websh.exceptions.NoValidRequest;
import com.example.websh.repository.TestRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Getter
public class TestService {

    private final TestRepository testRepository;


    @Autowired
    public TestService(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

   private TestEntity testEntityDefoult = TestEntity.builder().testComment("нет записи")
            .id(UUID.randomUUID())
            .testLogin("Defoult")
            .testName("Defoult")
            .testPasswordUser(123)
            .testRoleUser("Role")
            .testDataCreateUser(LocalDateTime.now())
            .build();

    public TestEntity randomTestEntity() {

        return  TestEntity.builder().testComment("нет записи")
                .id(UUID.randomUUID())
                .testLogin(UUID.randomUUID().toString().substring(0, 4))
                .testName(UUID.randomUUID().toString().substring(0, 4))
                .testPasswordUser((int) (Math.random() * 1000) )
                .testRoleUser("Role")
                .testDataCreateUser(LocalDateTime.now())
                .build();

    }

    @Transactional
    public TestEntity getTestEntityById(UUID id){

        return Optional.ofNullable(testRepository.getTestEntityById(id)).orElse(testEntityDefoult);
    }
    @Transactional
    public void saveTestEntity(UUID id, TestEntity newTestEntity){

        TestEntity TestEntityFromBD = testRepository.getTestEntityById(id);//поиск по id в бд существует ли запись
        if (TestEntityFromBD != null){
            testRepository.merge(newTestEntity);//обновляем данные в бд если есть запись с таким id
        }
        else {
                testRepository.saveTestEntity(newTestEntity);
            }
        }

    @Transactional
    public void save(TestEntity testEntity){
            testRepository.saveTestEntity(testEntity);

    }

    public List<TestEntity> getAllTestEntity() {
        return testRepository.findAllTestEntity();
    }

    @Transactional
    public void deleteTestDataById(String id) {
        testRepository.deleteTestEntityById(UUID.fromString(id));
    }
}
