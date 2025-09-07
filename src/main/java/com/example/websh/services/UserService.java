package com.example.websh.services;

import com.example.websh.dto.ProductDto;
import com.example.websh.dto.UserDto;
import com.example.websh.entity.ProductEntity;
import com.example.websh.entity.UsersEntity;
import com.example.websh.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Objects.nonNull;


/**
 * Класс для работы с пользователями для security
 * берет пользователя из БД
 */
@Service
@RequiredArgsConstructor
    public class   UserService {

    private final UserRepository userRepository;


    @Value("${pathForSaveFile3D}")
    String pathToSaveFile3D; // путь для сохранения файлов

    public UserDto getUserById(String uuidUser) {

        UsersEntity usersEntity = userRepository.findById(UUID.fromString(uuidUser)).orElse(null);// пользователь из БД

        if (usersEntity != null) {
            return UserDto.builder()
                    .id(usersEntity.getId())
                    .dataCreateUser(usersEntity.getDataCreateUser())
                    .mail(usersEntity.getMail())
                    .comment(usersEntity.getComment())
                    .userName(usersEntity.getUserName())
                    .boolverify(usersEntity.isBoolverify())
                    .login(usersEntity.getLogin())
                    .roleUser(usersEntity.getRoleUser())
                    .psswordUser(usersEntity.getPsswordUser())
                    .build();
        }
        return null;
    }

    /**
     * Переопределенный метод получения данных пользователя из БД и перевод их в UserDetails
     * метод будет вызван автоматически при вводе логина в форме ввода "логин - пароль" от security
     */

    public UserDto loadUserByUserLogin(String userLogin) {

        UsersEntity user = userRepository.findByLogin(userLogin).orElse(null);// пользователь из БД

        if (user!= null) {
            return UserDto.builder()
                    .mail(user.getMail())
                    .dataCreateUser(user.getDataCreateUser())
                    .boolverify(user.isBoolverify())
                    .comment(user.getComment())
                    .roleUser(user.getRoleUser())
                    .psswordUser(user.getPsswordUser())
                    .userName(user.getUserName())
                    .login(user.getLogin())
                    .id(user.getId())
                    .build();
        }
        return null;
    }

    @Transactional
    public UserDto saveUser(UserDto userDto) {

            UUID uuidForSave = userDto.getId();
            UsersEntity userSave;

// если id null то создать нового пользователя
            if (Objects.isNull(uuidForSave)) {

                userSave = userRepository.save(UsersEntity.builder()
                        .userName(userDto.getUserName())
                        .comment(userDto.getComment())
                        .mail(userDto.getMail())
                        .dataCreateUser(LocalDateTime.now())
                        .psswordUser(userDto.getPsswordUser())
                        .boolverify(userDto.isBoolverify())
                        .login(userDto.getLogin())
                        .roleUser(userDto.getRoleUser())
                        .build());
                // новый id для сохраненной сущности
//                uuidForSave = userSave.getId();
            }
            else {
                //получить пользователя из БД по id
                userSave = userRepository.findById(userDto.getId()).orElse(null);
                // если существует пользователь в БД то обновить поля
                if(nonNull(userSave)) {

                    userSave.setPsswordUser(userDto.getPsswordUser());
                    userSave.setRoleUser(userDto.getRoleUser());
                    userSave.setUserName(userDto.getUserName());
                    userSave.setBoolverify(userDto.isBoolverify());
                    userSave.setComment(userDto.getComment());
                    userSave.setMail(userDto.getMail());
                    userSave.setLogin(userDto.getLogin());

                    userRepository.save(userSave);
                }
                else {
                    //если пользователя по id нет в БД то создать
                    userSave = new UsersEntity();

                    if (userDto.getPsswordUser() == null) {
                        userDto.setPsswordUser("user");
                    } else {
                        userDto.setPsswordUser(userDto.getPsswordUser());
                    }

                    userSave.setPsswordUser(userDto.getPsswordUser());
                    userSave.setRoleUser(userDto.getRoleUser());
                    userSave.setUserName(userDto.getUserName());
                    userSave.setBoolverify(userDto.isBoolverify());
                    userSave.setComment(userDto.getComment());
                    userSave.setMail(userDto.getMail());
                    if (userDto.getLogin().equals("Регистрация")) {
                        userSave.setLogin("Логин: " + UUID.randomUUID().toString().substring(0, 8) + " Ваш пароль: \"user\"");
                    } else {
                        userSave.setLogin(userDto.getLogin());
                    }
//                    userBD.setId(userDto.getId());
                    userRepository.save(userSave);
                }
                }

// возвращение UserDto
            return UserDto.builder()
                    .mail(userSave.getMail())
                    .dataCreateUser(userSave.getDataCreateUser())
                    .boolverify(userSave.isBoolverify())
                    .comment(userSave.getComment())
                    .roleUser(userSave.getRoleUser())
                    .psswordUser(userSave.getPsswordUser())
                    .userName(userSave.getUserName())
                    .login(userSave.getLogin())
                    .id(userSave.getId())
                    .build();

    }

// Получение всех пользователей и мап в дто
    public List<UserDto> getAllUsers() {
        List<UserDto> userDtoList = new ArrayList<>();

        List<UsersEntity> usersEntityList = userRepository.findAll();

        if (!usersEntityList.isEmpty()){
            for (UsersEntity user: usersEntityList){
                userDtoList.add(
                UserDto.builder()
                        .mail(user.getMail())
                        .dataCreateUser(user.getDataCreateUser())
                        .boolverify(user.isBoolverify())
                        .comment(user.getComment())
                        .roleUser(user.getRoleUser())
                        .psswordUser(user.getPsswordUser())
                        .userName(user.getUserName())
                        .login(user.getLogin())
                        .id(user.getId())
                        .build());
            }
        }

        return userDtoList;
    }



}