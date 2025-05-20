package com.example.websh.controllers;

import com.example.websh.dto.UserDtoAuthen;
import com.example.websh.entity.TestEntity;
import com.example.websh.services.TestService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }


    @GetMapping("/")
    public String index() {
        return "index BACKEND 123123132\n 123123132\n123123132\n123123132\n123123132\n";
    }

    @PostMapping("/api/test_request_on_back")
    public HttpEntity<String> testRequest() {


        HttpEntity<String> httpEntity = new HttpEntity<>("тестовый запрос от бекенда получен на фронт\n");
        return httpEntity;
    }

    @GetMapping("/api/get_testData/{id}")
    public TestEntity getTestData(@PathVariable("id") UUID id) {

        return testService.getTestEntityById(id);

    }

    @PutMapping("/api/save_test_data/{id}")
    ResponseEntity<TestEntity> saveChangeTestEntity(@RequestBody TestEntity testEntity, @PathVariable("id") UUID id){

        testService.saveTestEntity(id, testEntity);
        return ResponseEntity.ok().body(testEntity);
    }

    @GetMapping("/save")
    public TestEntity save() {
     TestEntity entity = testService.randomTestEntity();
     testService.save(entity);
     return entity;
    }

    @GetMapping("/api/test_entity")
        public List<TestEntity> getAllTestEntity(){
            return testService.getAllTestEntity();
        }


    @GetMapping("/api/delete/test_data/{id}")
    public ResponseEntity<String> deleteTestDataById(@PathVariable String id){
        testService.deleteTestDataById(id);
        return new ResponseEntity<>("удалено Entity:  " + id, HttpStatus.OK);
    }

//    @GetMapping("/api/authorize_user")
//    public ResponseEntity<?> authorize(/*@RequestBody UserDtoAuthen userDtoAuthen*/) {
//
//        return ResponseEntity.ok("@GetMapping(\"/api/authorize_user\")");
//    }


}

