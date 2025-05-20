package com.example.websh.services;

import com.example.websh.entity.UsersEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Создание и проверка jwt токена
 */
@Service
public class JwtService {

    private Key key;
    private final Duration jwtLifeTime =Duration.ofMinutes(30); //время жизни токена 30 мин

    @PostConstruct
    public void initialize() {
        //ключ шифрования токена
        String secret = "mykeyfggdfgdfewefdsdsgfdfdshhfadffdgfsfgfdhdfhd";
        key = Keys.hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8)); //ключ для шифрования токена на основе секрет
    }

    /**
     * Генерация токена с информацией об usersEntity
     */
    public String generateJWToken(UsersEntity usersEntity) {
        Map<String, String> payLoadToken = new HashMap<>(); // полезная нагрузка для токена
        payLoadToken.put("name", usersEntity.getUserName());
        payLoadToken.put("role", usersEntity.getRoleUser());

        Date issuedDate = new Date(); // время создания токена - текущее время
        Date expiriedDate = new Date(issuedDate.getTime() + jwtLifeTime.toMillis());  // время жизни токена



        return Jwts.builder()
                .claims(payLoadToken) // полезная нагрузка (список Map)
                .claim("sub", "userEntity") // тема токена
                .claim("iat", issuedDate.getTime() / 1000)       // время создания секунды
                .claim("exp", expiriedDate.getTime() / 1000)     // время жизни секунды
                .signWith(key)               // Подписание токена ключем
                .compact();
    }

    /**
     * Ппарсинг токена - получение Claims
     * @param token
     * @return
     */
    private Claims getAllClaimsToken(String token){
        Jws<Claims> parsedToken = Jwts.parser()
                .setSigningKey(key)        // Задаем ключ для проверки подписи
                .build()
                .parseClaimsJws(token);    // Парсим токен

        return parsedToken.getBody();

    }

    //получение имени пользователя из токена
    public String getUserName(String token){
        return getAllClaimsToken(token).get("name", String.class);
    }

    //получение роли пользователя из токена
    //todo вщзможно не так
    public String getUserRole(String token) {
        return getAllClaimsToken(token).get("role", String.class);
    }


}
