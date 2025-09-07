package com.example.websh.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice // перехватчик ошибок в контроллере, в фильтрах не срабатывает
public class ExceptionHandler {
    /**
     * Обработчик ошибок типа NoValidRequest
     * @param noValidRequest
     * @return
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(NoValidRequest.class)
    public ResponseEntity<?> NoValidRequestException(NoValidRequest noValidRequest){
            return ResponseEntity.badRequest().body(noValidRequest.getMessage());//при ошибке вернем ResponseEntity с  сообщением ошибки
    }

}
