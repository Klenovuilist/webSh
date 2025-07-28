package com.example.websh.controllers;

import com.example.websh.dto.GroupProductDto;
import com.example.websh.services.GroupsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final GroupsService groupsService;

    private List<GroupProductDto> groupList = new ArrayList<>();

    @PutMapping("/api/create_group/{id}")
    void createGroup(@PathVariable("id") String parrentUuid){
// создание групп
        groupsService.crateNewGroup(parrentUuid);

        //обновление листа групп
        groupList = groupsService.getListGroup();

    }

    /**
     * получение List<GroupProductDto> для фронта
     * */
    @GetMapping("/api/get_list_group")
    public ResponseEntity<List<GroupProductDto>> getListGroup(){

        if (groupList.isEmpty()){
            groupList = groupsService.getListGroup();
        }

        return ResponseEntity.ok(groupList);
    }

    @DeleteMapping("/api/del_group/{id}")
    void deleteGroup(@PathVariable("id") String uuid){
// создание групп
        groupsService.deleteGroup(uuid);

        //обновление листа групп
        groupList = groupsService.getListGroup();

    }

    @PutMapping("/api/change_group/")
    void сhangeGroup(@RequestBody GroupProductDto groupDto){
//
        groupsService.updateGroupEntity(groupDto);

        //обновление листа групп
        groupList = groupsService.getListGroup();
    }
}
