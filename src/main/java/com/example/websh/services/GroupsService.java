package com.example.websh.services;

import com.example.websh.copmparator.ComparatorGroup;
import com.example.websh.dto.GroupProductDto;
import com.example.websh.entity.GroupProductEntity;
import com.example.websh.entity.ProductEntity;
import com.example.websh.exceptions.ExceptionRepository;
import com.example.websh.repository.GroupRepository;
import com.example.websh.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupsService {

    private final GroupRepository groupRepository;

    private final ProductRepository productRepository;


    private int i = 1; //счетчик номера вновь созданной группы

    /**
     * Получение развернутого листа GroupProductDto
     * */
    public List<GroupProductDto> getListGroup(){

       List<GroupProductEntity> listGroupEntity = groupRepository.findAll();

       //если база пустая то создать группу по умолчанию
       if (listGroupEntity.isEmpty()){
           return defoultGruopDto();
       }

       ComparatorGroup comparatorGroup = new ComparatorGroup(); // компаратор для сравнения по префиксу имени группы

       List<GroupProductEntity> sortListGroupEntity = listGroupEntity.stream().sorted(comparatorGroup).collect(Collectors.toList());

//       List<GroupProductDto> groupProductDtoList = getStructureListGroup(mapGroupAllEntityListToDto(sortListGroupEntity));


//       return groupProductDtoList;
        return mapGroupAllEntityListToDto(sortListGroupEntity);
    }

    /**
     * Получение дефолтной группы дто если в база групп пуста
     */
   private List<GroupProductDto> defoultGruopDto(){

        List<GroupProductDto> listGroupProduct = new ArrayList<>();

        //сохранение и получение uuid первой группы
        UUID parrentUuidCat = groupRepository.save(GroupProductEntity.builder()
                .groupName("Кошки")
                .levelGroup(0)
                .build()).getGroupId();

        groupRepository.save(GroupProductEntity.builder()
                .groupName("Радио Кошки")
                .levelGroup(1)
                .parrentId(parrentUuidCat)
                .build());

        UUID parrentUuidCatMehanic = groupRepository.save(GroupProductEntity.builder()
               .groupName("Механические Кошки")
               .levelGroup(1)
               .parrentId(parrentUuidCat)
               .build()).getGroupId();

       groupRepository.save(GroupProductEntity.builder()
               .groupName("простые Механические Кошки")
               .levelGroup(2)
               .parrentId(parrentUuidCatMehanic)
               .build());

       groupRepository.save(GroupProductEntity.builder()
               .groupName("Мышки")
               .levelGroup(0)
               .build());

       groupRepository.save(GroupProductEntity.builder()
               .groupName("Хомяки")
               .levelGroup(0)
               .build());

       // структурированный лист
        return  getStructureListGroup(mapGroupAllEntityListToDto(groupRepository.findAll()));

    }


    /**
     * Развертывание листа <GroupProductDto> групп с подгруппами для вывода на фронт
     */
    private List<GroupProductDto> getStructureListGroup(List<GroupProductDto> list) {
        List<GroupProductDto> resultList = new ArrayList<>();
        structureList(list, resultList);
        return resultList;
    }

    /**
     * Структурирование листа с рекурсией (плоский список из групп и подгрупп)
     */
    private void structureList(List<GroupProductDto> list, List<GroupProductDto> resultList) {

        int i = 0;

        if(list.isEmpty()){
            return;
        }

        while (i < list.size()) {
            GroupProductDto currentGroup = list.get(i);
            resultList.add(currentGroup); // добавление текущей группы в лист

            if (!currentGroup.getListUnderGroups().isEmpty()) { //проверка есть ли в листе групп подгруппы
                structureList(currentGroup.getListUnderGroups(), resultList); // рекурсия функции для листа с подгруппами
            }
            i++;
        }
    }


    /**
     * преобразование в List<GroupProductDto> из List<GroupProductEntity>
     */
    private List<GroupProductDto> mapGroupAllEntityListToDto(List<GroupProductEntity> listGroupEntities){

        List<GroupProductDto>listGroupDto = new ArrayList<>();
        //преобразование сущностей в дто
        listGroupEntities.forEach(groupEntities ->{
                                listGroupDto.add(mapGroupEntityToDto(groupEntities));
                                });
////добавление в листы родителей дочерниние группы
//        for (GroupProductDto groupDto: listGroupDto){
//                    if (Objects.isNull(groupDto.getParrentId())) {
//                        continue;
//                    }
//                    //поиск родителя
//                    Optional<GroupProductDto> optionalGroupDto = listGroupDto.stream()
//                            .filter(gr -> gr.getGroupId().equals(groupDto.getParrentId()))
//                            .findFirst();
//
//                     //добавление группы в родителя
//                            optionalGroupDto
//                                    .ifPresent(gr -> gr.getListUnderGroups().add(groupDto));
//        }
//        //лист GroupProductDto только из начальных групп
//         List<GroupProductDto> listGroupDtoResult = listGroupDto.stream()
//                 .filter(gr -> Objects.isNull(gr.getParrentId())).toList();

//        return listGroupDtoResult;
        return listGroupDto;
    }


    /**
     * преобразование в GroupProductDto> из GroupProductEntity
     */

    private GroupProductDto mapGroupEntityToDto(GroupProductEntity groupEntity){

        return GroupProductDto.builder()
                .parrentId(groupEntity.getParrentId())
                .groupId(groupEntity.getGroupId())
                .levelGroup(groupEntity.getLevelGroup())
                .groupName(groupEntity.getGroupName())
                .build();

    }



    /**
     * Создание новой группы
     * */
    public UUID crateNewGroup(String newParrentUuid){

        GroupProductEntity group = GroupProductEntity.builder()
                .groupName("Новая группа " + i)
                .levelGroup(0)
                .build();

        if(! newParrentUuid.equals("0")) {
            Optional<GroupProductEntity> parrentGroup = groupRepository.findById(UUID.fromString(newParrentUuid));

            parrentGroup
                    .ifPresent(parrentEntity -> {
                        group.setParrentId(parrentEntity.getGroupId());
                        group.setLevelGroup(parrentEntity.getLevelGroup() + 1);
                    });

        }

            try {
                groupRepository.save(group);
                i++;
                return group.getGroupId();

            } catch (RuntimeException e) {
                throw new ExceptionRepository("Ошибка создания(сохранения) корневой Новой группы = " + i);
            }

        }

    /**
     * Удаление группы
     * */
    @Transactional
    public void deleteGroup(String uuid) {


        List<GroupProductEntity>listFromBD = groupRepository.findAll();

        // группа для удаления
        GroupProductEntity groupDel =  listFromBD.stream()
                .filter(gr -> gr.getGroupId().equals(UUID.fromString(uuid)))
                .findFirst().orElse(null);
        if (Objects.isNull(groupDel)){
            return;
        }
        UUID uuidParent = groupDel.getParrentId(); // id родителя группы на удаление

// дочерняя группа при наличии
        GroupProductEntity childGroup = listFromBD.stream()
                .filter(gr ->Objects.nonNull(gr.getParrentId()) && gr.getParrentId().equals(UUID.fromString(uuid)))
                .findFirst()
                .orElse(null);

        if(Objects.isNull(childGroup)){
            groupRepository.deleteById(UUID.fromString(uuid));
        }
        else {
            childGroup.setParrentId(uuidParent);
            listFromBD.remove(groupDel);
            listFromBD = updateLevelGroup(listFromBD);

            groupRepository.saveAll(listFromBD);
            groupRepository.deleteById(UUID.fromString(uuid));
        }

        //получить продукты и установить или обнулить группу
        List<ProductEntity> listProductEntity = productRepository.findAllByGroupsId(UUID.fromString(uuid));
        if (listProductEntity.isEmpty()){
            return;
        }
        //создание если нет свалки, куда переложить продукты без группы
        if (Objects.isNull(uuidParent)){
            GroupProductEntity svalka = groupRepository.findByNameGroup("Свалка").orElse(null);

            if(Objects.isNull(svalka)){
                svalka = GroupProductEntity.builder()
                        .groupName("Свалка")
                        .levelGroup(0)
                        .build();
                groupRepository.save(svalka);  // сохранить новую свалку
            }

            uuidParent = svalka.getGroupId();
        }
        for (ProductEntity product: listProductEntity){ // установка новых значений групп для продуктов
                product.setGroupsId(uuidParent);
        }
        productRepository.saveAll(listProductEntity);
        productRepository.flush();

        }


    /**
     * обновление группы
     * */

    @Transactional
    public void updateGroupEntity(GroupProductDto groupDto) {

        UUID groupId = groupDto.getGroupId();

        String newGroupName = groupDto.getGroupName();

        UUID newParrentId = groupDto.getParrentId();

//        UUID newSlaveId = groupDto.getSlaveId();

        int newLevelGroup = 0;


        List<GroupProductEntity> entityList = groupRepository.findAll();

        if (entityList.isEmpty()) {
            return;
        }

// группа для изменения Optional
        Optional<GroupProductEntity> optGroupEntityForChange = entityList.stream()
                .filter(entity -> entity.getGroupId().equals(groupId)).findFirst();

        GroupProductEntity groupChange;
        // группа для изменения
        if (optGroupEntityForChange.isPresent()) {
            groupChange = optGroupEntityForChange.get();
        } else {
            return;
        }

        if (newGroupName != null) {
            groupChange.setGroupName(newGroupName);
        }

        if(newParrentId == null){
            groupChange.setParrentId(null);
            groupChange.setLevelGroup(0);
            groupRepository.saveAll(updateLevelGroup(entityList));
            groupRepository.flush();

        }

      //Обновляем родителя если:
        if (newParrentId != null && ! newParrentId.equals(groupId)) {
            // новый родитель из бд
            GroupProductEntity newParrent = entityList.stream()
                    .filter(entity -> entity.getGroupId().equals(newParrentId))
                    .findFirst().get();

            UUID currentParrentUUID = newParrent.getParrentId(); //UUiD родителя текущего родителя
            UUID currentChildUUID = newParrent.getGroupId();  //UUiD текщей группы
            GroupProductEntity currentParrent = newParrent; //текущий родитель
            GroupProductEntity currentChild = newParrent; //текущий родитель
            boolean inOneGroup = false;   // изменяемая группа оказывается в цепочке нового родителя

            // проход по цепочке от нового родителя до корневого элемента
            // если по пути будет
            while (currentParrentUUID != null) {


                //получение предыдущего родителя
             for (GroupProductEntity gr: entityList){

                 //если текущий родитель равен предыдущему
                 if(currentParrent.getParrentId().equals(gr.getGroupId())) {
                     currentChild = currentParrent; //новый дочерний элемент
                     currentChildUUID = currentParrentUUID; //новый id дочернего элемента
                     currentParrent = gr; //новый родитель
                     currentParrentUUID = gr.getParrentId(); //новый id родителя
                     break;
                 }
             }

                // если изменяемая группа оказывается в цепочке родителей для нового родителя
                if (currentParrent.getGroupId().equals(groupId)) {
                    inOneGroup = true;
                    break;
                }
            }// end while

            //назначение дочернему элементу изменяемой группы нового родителя
            if (inOneGroup) {
                currentChild.setParrentId(groupChange.getParrentId());
                currentChild.setLevelGroup(groupChange.getLevelGroup());
            }
            groupChange.setParrentId(newParrentId);
            groupChange.setLevelGroup(newParrent.getLevelGroup() + 1);


//            if (newSlaveId != null) {
//                optGroupEntityForChange.get().setSlaveId(newSlaveId);
//            }

            groupRepository.saveAll(updateLevelGroup(entityList));
            groupRepository.flush();

//            groupRepository.saveAll(entityList); // сохранить сущности

        }
    }


    /**
     * изменение уровня групп
     * */
    private List<GroupProductEntity> updateLevelGroup(List<GroupProductEntity> noUpdateList){

        List<GroupProductEntity> resultList = new ArrayList<>();

       List<GroupProductEntity> currentList = noUpdateList.stream()
                .filter(gr -> gr.getParrentId() == null)
                .peek(gr -> gr.setLevelGroup(0))
                .collect(Collectors.toList());

        while (resultList.size() < noUpdateList.size()){
            if (currentList.isEmpty()) {
                break;
            }
            List<GroupProductEntity> currentUnderList = new ArrayList<>();

            for (GroupProductEntity group: currentList){

                currentUnderList.addAll(noUpdateList.stream()
                        .filter(gr -> Objects.nonNull(gr.getParrentId()))
                      .filter(gr -> gr.getParrentId().equals(group.getGroupId()))
                        .peek(gr -> gr.setLevelGroup(group.getLevelGroup() + 1))
                      .toList());
                }
            resultList.addAll(currentList);
            currentList = currentUnderList;
            }

            return resultList;
        }

    /**
     * Получить группу по id
     */
    public GroupProductDto getGroupById(UUID groupsId) {

        if (Objects.isNull(groupsId)){
            return null;
        }

        GroupProductEntity groupEntity = groupRepository.findById(groupsId).orElse(null);

        if(Objects.nonNull(groupEntity)){

            return mapGroupEntityToDto(groupEntity);
        }
        return new GroupProductDto();
    }
}

