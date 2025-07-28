package com.example.websh.services;

import com.example.websh.entity.UsersEntity;
import com.example.websh.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Класс для работы с пользователями для security
 * берет пользователя из БД
 */
@Service
@AllArgsConstructor
    public class    UserService implements UserDetailsService {

        private final UserRepository userRepository;

        /**
     * Переопределенный метод получения данных пользователя из БД и перевод их в UserDetails
     * метод будет вызван автоматически при вводе логина в форме ввода "логин - пароль" от security
      */
    @Override
    public UserDetails loadUserByUsername(String userLogin) throws UsernameNotFoundException {

         UsersEntity usersEntity = userRepository.findByLogin(userLogin).orElse(null);// пользователь из БД

        if(usersEntity != null) {
           return new User( // наследник от UserDetails

                    usersEntity.getLogin(),//имя
                    usersEntity.getPsswordUser(), // пароль, будет автоматически дехеширован
                    Collections.singletonList(new SimpleGrantedAuthority(usersEntity.getRoleUser())));// коллекция объектов GrantedAuthority, содержащих роли пользователя
        }
        return null;
    }

//todo при необходимости задать метод создания пользовател
    public void createNewUser(UsersEntity usersEntity){
        usersEntity.setRoleUser("ROLE_USER");
    }

    // пользователь из БД
    public UsersEntity getUserByLogin(String userLogin){

        UsersEntity usersEntity = userRepository.findByLogin(userLogin).orElse(null);// пользователь из БД
        return usersEntity;
    }
}
