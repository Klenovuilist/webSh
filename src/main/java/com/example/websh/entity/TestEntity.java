package com.example.websh.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity()
@Table(name = "testdata")
@Builder
@Getter
@Setter
@AllArgsConstructor
public class TestEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "test_name")
    private String testName;

    @Column(name = "test_login")
    private String testLogin;

    @Column(name = "test_password_user")
    private Integer testPasswordUser;

    @Column(name = "test_role_user")
    private String testRoleUser;


    @Column(name = "test_comment")
    private String testComment;

    @Column(name = "test_data_create_user")
    @CreationTimestamp
    private LocalDateTime testDataCreateUser;

    public TestEntity() {

    }

//    @Transient
//    private String dataCreateParsing;



}
