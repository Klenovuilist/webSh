package com.example.websh.repository;


import com.example.websh.entity.GroupProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<GroupProductEntity, UUID> {


}
