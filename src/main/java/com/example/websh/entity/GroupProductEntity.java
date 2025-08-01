package com.example.websh.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@Table(name = "groups_product")

public class GroupProductEntity {

    public GroupProductEntity() {

    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID groupId;


    @Column(name = "name_group")
    private String groupName;

    @Column(name = "id_parrent")
    private UUID parrentId;

    @Column(name = "id_slave")
    private UUID slaveId;

    @Column(name = "level_group")
    private int levelGroup;





}
