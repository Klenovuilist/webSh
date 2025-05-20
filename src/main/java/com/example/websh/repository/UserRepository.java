package com.example.websh.repository;

import com.example.websh.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UsersEntity, UUID> {
//    void findByUserName(String username);
    Optional<UsersEntity> findByUserName(String userName);

    Optional<UsersEntity> findByLogin(String userName);


}
