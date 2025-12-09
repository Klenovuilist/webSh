package com.example.websh.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(of = {"productId"})
@Table(name = "products")

public class ProductEntity {

    public ProductEntity() {

    }

    @Id
    @Column(name = "id")
//    @GeneratedValue(strategy = GenerationType.IDENTITY) //не генерится в базе
    private UUID productId;

    @Column(name = "product_name")
    private String product_name;

    @Column(name = "producct_category")
    private String productCategory;

    @Column(name = "product_articul")
    private String productArticul;

    @Column(name = "product_reference")
    private String productReference;

    @Column(name = "product_description")
    private String productDescription;

    @Column(name = "product_coast")
    private String productCoast;

    @Column(name = "product_count")
    private String productCount;

    @Column(name = "teg")
    private String teg;

    @Column(name = "view_image")
    private String view_image;

    @Column(name = "product_reserv")
    private int productReserv;

    @ManyToMany(mappedBy = "productEntity")
    private List<UsersEntity> usersEntity;

    @Column(name = "id_groups")
    private UUID groupsId;

    @Column(name = "is_load")
    private String isLoad;


    @Column(name = "data_create_product")
    private LocalDateTime data_create_product;



}
