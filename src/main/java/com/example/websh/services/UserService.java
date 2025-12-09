package com.example.websh.services;

import com.example.websh.dto.ProductDto;
import com.example.websh.dto.UserDto;
import com.example.websh.entity.File3DEntity;
import com.example.websh.entity.ProductEntity;
import com.example.websh.entity.UsersEntity;
import com.example.websh.repository.FileRepository;
import com.example.websh.repository.ProductRepository;
import com.example.websh.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;


/**
 * Класс для работы с пользователями для security
 * берет пользователя из БД
 */
@Service
@RequiredArgsConstructor
    public class   UserService {

    private final UserRepository userRepository;

    private final ProductService productService;

    private final ProductRepository productRepository;

    private final FileRepository fileRepository;




    @Value("${pathForSaveFile3D}")
    String pathToSaveFile3D; // путь для сохранения файлов


    /**
     * user по id
     */
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
                    .productsDTO(productService.mapListProductEntityToDTO(usersEntity.getProductEntity()))
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
                    .productsDTO(productService.mapListProductEntityToDTO(user.getProductEntity()))
                    .build();
        }
        return null;
    }

    @Transactional
    public UserDto saveUser(UserDto userDto) {

            UUID uuidForSave = userDto.getId();
            UsersEntity userSave;

// если id null то создать нового пользователя (при регистрации возможно)
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
                        .productEntity(new ArrayList<>()) // пустой лист продуктов
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

                    if(nonNull(userDto.getProductsDTO()) && ! userDto.getProductsDTO().isEmpty()){
                        userSave.setProductEntity(productService.mapListProductDtoToEntity(userDto.getProductsDTO()));
                    }

                    userRepository.save(userSave);
                }
                else {
                    //если пользователя по id нет в БД то создать нового случайного
                    userSave = new UsersEntity();
                    // случайный пароль если не задан
                    if (userDto.getPsswordUser() == null) {
                        userDto.setPsswordUser("psw" + UUID.randomUUID().toString().substring(0, 4));
                    } else {
                        userDto.setPsswordUser(userDto.getPsswordUser());
                    }

                    userSave.setPsswordUser(userDto.getPsswordUser());
                    userSave.setRoleUser(userDto.getRoleUser());
                    userSave.setUserName(userDto.getUserName());
                    userSave.setBoolverify(userDto.isBoolverify());
                    userSave.setComment(userDto.getComment());
                    userSave.setMail(userDto.getMail());
                    userSave.setProductEntity(new ArrayList<>()); // пустой лист продуктов

                    // если логин "Регистрация" (по умолчанию) то сгенерировать случайный логин
                    if (userDto.getLogin().equals("Регистрация")) {
                        userSave.setLogin(UUID.randomUUID().toString().substring(0, 8));
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
                    .productsDTO(productService.mapListProductEntityToDTO(userSave.getProductEntity()))
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

    /**
     * Преобразование userDto в Entity
     */
    public UsersEntity userDtoToEntity(UserDto userDto) {
        List<ProductEntity> productEntityList = new ArrayList<>();

        if(nonNull(userDto.getProductsDTO())) {
            productEntityList = userDto.getProductsDTO().stream()
                    .map(productDto -> productService.productDtoToEntity(productDto)).toList();
        }

        return UsersEntity.builder()
                .userName(userDto.getUserName())
                .id(userDto.getId())
                .psswordUser(userDto.getPsswordUser())
                .dataCreateUser(userDto.getDataCreateUser())
                .comment(userDto.getComment())
                .roleUser(userDto.getRoleUser())
                .login(userDto.getLogin())
                .boolverify(userDto.isBoolverify())
                .productEntity(productEntityList)
                .mail(userDto.getMail())
                .build();
    }


    /**
     * Добавить новые продукты из листа UserDto в UsersEntity из БД
     * @param userDto
     */
    public void addProductsToUserEntity(UserDto userDto){

        UsersEntity userForUpdate = userRepository.findById(userDto.getId()).orElse(null);
        if (isNull(userForUpdate)){
            return;
        }

        for(ProductDto productDto: userDto.getProductsDTO()){
            Optional<ProductEntity> prodOpt = productRepository.findById(productDto.getProductId());

            prodOpt.ifPresent(product -> {
//                if(! userForUpdate.getProductEntity().contains(prodOpt.get())){
                    userForUpdate.getProductEntity().add(product);
//                }
            });
        }
        userRepository.save(userForUpdate);


    }


    /**
     * удалить пользователя по id
     */
    @Transactional

    public void deleteUserById(String userId) {

        userRepository.deleteById(UUID.fromString(userId));
    }

    /**
     * Удаление пользователей не подттвержденных и не начавших работу с файлами File3DServices (isApproval() = false )
     */
    @Scheduled(cron = "0 33 3 * * ?", zone = "Europe/Moscow")
    @Transactional
    public void deleteNoJobUser() {
        List<UsersEntity> listUsersNoVerify = userRepository.findAllByBoolverifyIs(false);

        if(listUsersNoVerify.isEmpty()){
            System.out.println("Не рабочих пользователей нет");
            return;
        }

        List<UsersEntity>listUserForDelete = new ArrayList<>();

        for (UsersEntity currentUsersEntity: listUsersNoVerify){
            boolean userIdelete = false;

            List<File3DEntity>listFile3D = fileRepository.findByUserId(currentUsersEntity.getId());

            if(listFile3D.isEmpty()){
                listUserForDelete.add(currentUsersEntity);
//                userIdelete = true;
                continue;
            }
            //Поиск хотя бы одного файла isApproval() = true
            Optional<File3DEntity> file3DIsApproval = listFile3D.stream().filter(file -> file.isApproval()).findAny();

            if(file3DIsApproval.isPresent()){
                continue;
            }
            //если не нашлось ни одного файла в работе(подтвержденного) isApproval() = false то добавить user на удаление
            listUserForDelete.add(currentUsersEntity);
        }

        if(listUserForDelete.isEmpty()){
            System.out.println("Не рабочих пользователей нет");
            return;

        }
        //удаление всех пользователей и их File3DEntity
        for (UsersEntity usersEntityForDelete: listUserForDelete){
            fileRepository.deleteAllByUserId(usersEntityForDelete.getId());
            userRepository.delete(usersEntityForDelete);

        }

        System.out.println("Не рабочие пользователи удалены");
    }
}