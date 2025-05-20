package com.example.websh.controllers;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {



    @GetMapping("/")
    public String index(){
        return "index BACKEND 123123132\n 123123132\n123123132\n123123132\n123123132\n";
    }

    @PostMapping("/api/test_request_on_back")
    public HttpEntity<String> testRequest(){


        HttpEntity<String> httpEntity = new HttpEntity<>("тестовый запрос от бекенда получен на фронт\n");
        return httpEntity;
    }

}
