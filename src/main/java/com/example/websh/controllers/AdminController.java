package com.example.websh.controllers;

import com.example.websh.dto.GroupProductDto;
import com.example.websh.dto.ProductDto;
import com.example.websh.services.GroupsService;
import com.example.websh.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final GroupsService groupsService;

    private final ProductService productService;

    private List<GroupProductDto> groupList = new ArrayList<>();


    @Value("${pathForSaveImage}")
    String pathToSaveGroup; // путь для сохранения картинок групп

    @Value("${pathForSaveImageProduct}")
    String pathToSaveProduct; // путь для сохранения картинок групп


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


    /**
     * Получение картинки по UUID
     */
    @PostMapping("/api/image_group/{id}")
    public ResponseEntity<Resource> getImageById(@PathVariable("id") String uuid) {

        File folder = new File(pathToSaveGroup);// папка с файлами
        String extension = "";

        File fileImage = Arrays.stream(Objects.requireNonNull(folder.listFiles()))//картинка для группы по uuid
                .filter(f -> f.getName().contains(uuid)).findFirst().orElse(null);

        if(Objects.isNull(fileImage)){
            fileImage = Arrays.stream(Objects.requireNonNull(folder.listFiles())) //картинка по умолчанию
                    .filter(f -> f.getName().contains("no_image")).findFirst().orElse(null);
        }

        if(Objects.nonNull(fileImage)){
            int dotIndex = fileImage.getName().lastIndexOf('.');
            if (dotIndex > 0 && dotIndex < fileImage.getName().length() - 1) { // Проверка что точка существует и находится не в конце строки
                extension = fileImage.getName().substring(dotIndex); // расширение файла
            }
        }

        try {
            if (Objects.isNull(fileImage)){ //если картинки не найдено
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ByteArrayResource(new byte[]{}));
            }
            byte[] imageBytes = Files.readAllBytes(fileImage.toPath());  // массив байт из картинки

            Resource imageResource = new ByteArrayResource(imageBytes);  //ресурс для передачи на фронт

            HttpHeaders header = new HttpHeaders();
            if(! extension.isEmpty()){
                 // заголовок с расширением
                header.add("extension", extension);
            }

            return ResponseEntity.ok()
                    .headers(header)
                    .body(imageResource); //ресурс в тело ответа
        }
        catch (IOException ioEx) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ByteArrayResource(new byte[]{}));
        }

    }

    /**
     * Сохранение картинки группы на сервере
     */
    @PostMapping("/api/load_image_group/{id}")
    void loadImageGroup(@RequestBody byte[] imageBYTE // массив файла
            , @PathVariable("id") String uuidGroup // id группы
            , @RequestHeader("extension") String extension){ //заголовок расширение

        try {
            Path tempFile = Paths.get(pathToSaveGroup, uuidGroup + extension); //путь сохранения

            Files.write(tempFile, imageBYTE); // запись в файл  по указанному пути
            System.out.println("Изображение получено и сохранено: " + tempFile.toAbsolutePath());

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Получение картинки продукта по UUID
     */
    @PostMapping("api/image/product/{idProd}/{nameImage}")
    public ResponseEntity<Resource> getImageProductById(@PathVariable("idProd") String uuidProd
                            , @PathVariable("nameImage") String nameImage) {

        File folder = new File(pathToSaveProduct + uuidProd);//ссылка на папку с файлами картинок
        String extension = ""; //расширение по умолчанию
        HttpHeaders header = new HttpHeaders();
        File fileImage = null;


       //если папка существует то найти картинку
        if (folder.exists()){
            fileImage = Arrays.stream(Objects.requireNonNull(folder.listFiles()))//картинка для группы по uuid
                    .filter(f -> f.getName().startsWith(nameImage.split("\\.")[0]))
                    .findFirst().orElse(null);

            header.add("isExistImage", "ok"); //заголовок картинка существует

        }
        // если картинка не найдена, то взять из папки по умолчанию дефолтную картинку
        if(Objects.isNull(fileImage)){

            folder = new File(pathToSaveProduct);//ссылка на папку с файлами картинок

            fileImage = Arrays.stream(Objects.requireNonNull(folder.listFiles())) //картинка по умолчанию
                    .filter(f -> f.getName().contains("no_image")).findFirst().orElse(null);

            header.add("isExistImage", "no");
        }
        //если картинки не найдено
        if (Objects.isNull(fileImage)){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ByteArrayResource(new byte[]{}));
        }

            int dotIndex = fileImage.getName().lastIndexOf('.');
            if (dotIndex > 0 && dotIndex < fileImage.getName().length() - 1) { // Проверка что точка существует и находится не в конце строки
                extension = fileImage.getName().substring(dotIndex); // расширение файла
            }


        try {
            byte[] imageBytes = Files.readAllBytes(fileImage.toPath());  // массив байт из картинки

            Resource imageResource = new ByteArrayResource(imageBytes);  //ресурс для передачи на фронт

            if(! extension.isEmpty()){
                // заголовок с расширением
                header.add("extension", extension);
            }

            return ResponseEntity.ok()
                    .headers(header)
                    .body(imageResource); //ресурс в тело ответа
        }
        catch (IOException ioEx) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ByteArrayResource(new byte[]{}));
        }

    }

    /**
     * Получение продукта по UUID
     */
    @GetMapping("/api/product/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable("id") String uuidProduct){

        ProductDto productDto = productService.getProductById(uuidProduct);
        return ResponseEntity.ok(productDto);
            }

    /**
     * Получение листа продуктов по UUID группы
     */
    @GetMapping("/api/list_product/{uuidGroup}")
    public ResponseEntity<List<ProductDto>> getListProductByIdGroup(@PathVariable("uuidGroup") String uuidGroup){

        List<ProductDto> productDto = productService.getListProductByIdGroup(uuidGroup);
        return ResponseEntity.ok(productDto);
    }


    /**
     * Сохранение картинки продукта на сервере
     */
    @PostMapping("/api/load_image_product/{idprod}")
    void loadImageProduct(@RequestBody byte[] imageBYTE // массив файла
            , @PathVariable("idprod") String uuidProd // id группы
            , @RequestHeader("extension") String extension){ //заголовок расширение

        // путь до папки с картинками продукта
        Path directoryPath = Paths.get(pathToSaveProduct  + uuidProd);

        try {
            Files.createDirectories(directoryPath); // создание папки для картинок продукта если ее еще не было
        } catch (IOException e) {
            System.err.println("Ошибка при создании директории для картинок продукта: " + e.getMessage());
        }

        try {
            // сылка на папка с файлами картинок, для поиска существующих в ней файлов
            File folder = new File(pathToSaveProduct  + uuidProd);

            //сортированный лист существующих приставок для имен картинок продукта
            List<Integer> listPrefixNameImage = Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                    .map(file -> file.getName())
                    .map(str -> str.substring(0, str.lastIndexOf(".")))//получение имени
                    .map(Integer::parseInt).sorted().toList();

            int newPrefix = 1;

                if(! listPrefixNameImage.isEmpty()){
                    newPrefix = listPrefixNameImage.get(listPrefixNameImage.size() - 1) + 1;
                }

            //путь и имя файла сохранения картинки
            Path tempFile = Paths.get(pathToSaveProduct  + uuidProd, newPrefix + extension);

            Files.write(tempFile, imageBYTE); // запись в файл  по указанному пути
            System.out.println("Изображение сохранено: " + tempFile.toAbsolutePath());

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Получение листа имен картинок для продукта
     */
    @PostMapping("/api/ListNameImageProduct/{id}")
    public ResponseEntity<List<String>> getListNameImageProductById(@PathVariable("id") String uuid) {

        try {
            File folder = new File(pathToSaveProduct + uuid);// ссылка на папку с файлами картинок

            if (folder.exists()){
                // лист имен файлов для ID продукта
                List<String> listfileImage = Arrays.stream(Objects.requireNonNull(folder.listFiles()))//картинка для группы по uuid
                        .map(File::getName)
                        .toList();
                return ResponseEntity.ok(listfileImage);
            }
            else {
                // если папки нет то искать в родительской папке
                File folderDefoult = new File(pathToSaveProduct);// ссылка на папку с файлами картинок по умолчанию

                // лист имен файлов для ID продукта
                List<String> listfileImage = Arrays.stream(Objects.requireNonNull(folderDefoult.listFiles()))
                        .findFirst()
                        .map(file -> Collections.singletonList(file.getName())) // Получаем первый любой файл из списка
                        .orElse(Collections.emptyList());

                return ResponseEntity.ok(listfileImage);
            }
        }
        catch (RuntimeException e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }


    /**
     * Сохранить продукт и получить сохраненный из базы
     */
    @PostMapping("/api/save_product")
    public ResponseEntity<ProductDto> saveProduct(@RequestBody ProductDto productDto){

        productService.saveProduct(productDto);

        return ResponseEntity.ok(productService.getProductById(productDto.getProductId().toString()));
    }

    /**
     * Удалить продукт и получить группу ДТО
     */
    @PostMapping("/api/deletе_product/{idprod}")
    public ResponseEntity<GroupProductDto> deleteProduct(@PathVariable("idprod") String uuidProduct){

        // получение группы в которой удаляется продукт
      GroupProductDto groupDto = groupsService.getGroupById(productService.getProductById(uuidProduct).getGroupsId());

        productService.deleteProduct(uuidProduct); //удаление продукта

        return ResponseEntity.ok(groupDto);
    }
}
