package com.example.websh.repository;


import com.example.websh.entity.GroupProductEntity;
import com.example.websh.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<GroupProductEntity, UUID> {


    /**
     * Получение группы по имени
     */
    @Query(value = "SELECT * FROM groups_product WHERE groups_product.name_group = :name", nativeQuery = true)
    Optional<GroupProductEntity> findByNameGroup(@Param("name") String name);



    /**
     * Получение всех продуктов без группы
     */
    @Query(value = "select * FROM products  where products.id_groups is null", nativeQuery = true)
    List<ProductEntity> findAllByGroupsId();
}
