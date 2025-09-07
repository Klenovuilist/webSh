package com.example.websh.controllers;

import com.example.websh.dto.UserDto;

import com.example.websh.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


/**
 * Контроллер User
 */
@RestController
@AllArgsConstructor
public class AuthorizeController {

    private final UserService userService;


    /**
     *  получить пользователя по ID
     */
    @GetMapping("api/userId/{userId}")
    public ResponseEntity<UserDto> getUserById (@PathVariable("userId") String userId) {

        return ResponseEntity.ok(userService.getUserById(userId));
    }


    /**
     *  Сохранить пользователя
     */
    @PostMapping("api/save_user")
    public ResponseEntity<UserDto> saveUser (@RequestBody UserDto userDto) {

        return ResponseEntity.ok(userService.saveUser(userDto));

    }


    /**
     *  Сохранить пользователя
     */
    @PutMapping("api/update_user")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto) {

        return ResponseEntity.ok(userService.saveUser(userDto));

    }

/**
 *  получить пользователя по userLogin
 */
    @PostMapping("api/user/{userLogin}")
    public ResponseEntity<UserDto> getUserByLogin (@PathVariable("userLogin") String userLogin) {

        return ResponseEntity.ok(userService.loadUserByUserLogin(userLogin));

    }

    /**
     *  получить всех пользователей
     */
    @GetMapping("api/allUsers/")
    public ResponseEntity<List<UserDto>> getAllUsers() {

        return ResponseEntity.ok(userService.getAllUsers());

    }

}
