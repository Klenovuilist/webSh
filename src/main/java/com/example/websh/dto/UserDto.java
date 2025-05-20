package com.example.websh.dto;

import lombok.Data;


import java.time.LocalDateTime;

@Data
public class UserDto {

    private String userName;

    private String login;

    private String psswordUser;

    private String roleUser;

    private String comment;

    private LocalDateTime dataCreateUser;


}
