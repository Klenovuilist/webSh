//package com.example.websh.filters;
//
//import com.example.websh.exceptions.NoValidRequest;
//import com.example.websh.services.JwtService;
//import io.jsonwebtoken.ExpiredJwtException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Collections;
//
///**
// * Класс фильтр, перехватывает запрос от клиента, считывает токен, достает из него пользователя
// * помещает пользователя в контекст security, далее когда запрос дойдет до защищенного контроллера
// * в контексте будет пользователь с определенным набором ролей
// */
//
//@Component
//
//public class JwtFilter extends OncePerRequestFilter {
//
//    private final JwtService jwtService;
//
//    private String userName = null;
//    private String userRole = null;
//
//    public JwtFilter(JwtService jwtService) {
//        this.jwtService = jwtService;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String authHeader = request.getHeader("Authorization"); // получение заголовока в зпросе
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")){
//            String tokenJwt = authHeader.substring(7); // обзезка из заголовка "Bearer " и получение чистого токена
//
//        //считывание из заголовка имени и роли
//            try {
//                userName = jwtService.getUserName(tokenJwt);
//                userRole = jwtService.getUserRole(tokenJwt);
//            }
//            catch (io.jsonwebtoken.SignatureException ex){
//                response.addHeader("JWToken","no_valid");
//                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "токен некорректный"); // отправка ответа клиенту при ошибке
//                return;
////                throw new NoValidRequest("токен некорректный");
//
//            }
//            catch (ExpiredJwtException e) {
//                response.addHeader("JWToken","out_of_time");
//                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "время жизни токена вышло");
//                return;
////                throw new NoValidRequest("время жизни токена вышло");
//            }
//
//            // проверка наличия в контексте секьюрити объекта Authentication, если нет то создаем новго
//            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null){
//
//                //Объект для аутентификации
//                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
//                  userName
//                  ,null
//                  , Collections.singletonList(new SimpleGrantedAuthority(userRole))) {
//                };
//
//                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken); // установка в контекст секъюрити Authentication
//            }
//        }
//        filterChain.doFilter(request, response);// передача обработки запроса на следующий фильтр или контроллеру
//
//
//    }
//}
