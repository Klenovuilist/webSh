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
@Table(name = "users")
@Builder
@Getter
@Setter
@AllArgsConstructor
public class UsersEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "login")
    private String login;

    @Column(name = "password_user")
    private String psswordUser; // в БД в зашифрованном виде с помощью BCrypt

    @Column(name = "role_user")
    private String roleUser;


    @Column(name = "comment")
    private String comment;

    @Column(name = "data_create_user")
    @CreationTimestamp
    private LocalDateTime dataCreateUser;

    @Column(name = "boolverify")
    private boolean boolverify;

    @Column(name = "mail")
    private String mail;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "userid_productid",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<ProductEntity> productEntity;

    public UsersEntity() {

    }

//    @Transient
//    private String dataCreateParsing;



}
