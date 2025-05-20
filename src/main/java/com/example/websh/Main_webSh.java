package com.example.websh;

import com.example.websh.entity.ProductEntity;
import com.example.websh.entity.UsersEntity;
import com.example.websh.services.TestService;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@SpringBootApplication
public class Main_webSh {

    public static void main(String[] args) {

        ConfigurableApplicationContext contex = SpringApplication.run(Main_webSh.class, args);

        System.out.println("Загрузка контекста");


        UsersEntity usersEntity = UsersEntity.builder()
                .roleUser("ADMIN")
                .userName("websh")
                .comment("выводим все данные из таблицы")
                .dataCreateUser(LocalDateTime.now())
                .psswordUser(123)
                .build();


        ProductEntity productEntity = ProductEntity.builder()
//                .productId(UUID.randomUUID())
                .product_name("фиговина")
                .productCategory("хернюшки")
                .productArticul("арт.125")
                .productCount(10)
                .data_create_product(LocalDateTime.now())
                .productCoast(200.3)
                .productReserv(3)
                .productDescription("фиговина хернюшечная красивая")
                .productReference("www.dsdsdd")
                .usersEntity(new ArrayList<>(Arrays.asList(usersEntity)))
                .build();

usersEntity.setProductEntity(new ArrayList<>(Arrays.asList(productEntity)));

        TestService testService = contex.getBean(TestService.class);

        testService.getDataTest(usersEntity, productEntity);
    }



}
