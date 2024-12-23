package com.backend.lab1.service;

import com.backend.lab1.entity.User;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.json.Json;
import jakarta.ws.rs.core.Response;
import com.backend.lab1.repository.UserRepository;
import com.backend.lab1.service.JwtService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Stateless
public class AdminService {
    @EJB
    UserRepository userRepository;
    @EJB
    private JwtService jwtService;
    public String checkAdminByToken2(String authorizationHeader){
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return "Токен не предоставлен";
        }
        String token = authorizationHeader.substring("Bearer ".length()).trim();
        String login = jwtService.extractUserName(token);
        User user = userRepository.findByLogin(login);
        if (user == null || user.getRole() != User.Role.ADMIN) {
            return "Нет прав доступа";
        }
        return null;
    }
    public Response checkAdminByToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Json.createObjectBuilder()
                            .add("error", "Токен не предоставлен").build())
                    .build();
        }
        String token = authorizationHeader.substring("Bearer ".length()).trim();
        String login;
        try {
            login = jwtService.extractUserName(token);
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Json.createObjectBuilder()
                            .add("error", "Пользователя с таким токеном не существует").build())
                    .build();
        }
        User user = userRepository.findByLogin(login);
        if (user == null || user.getRole() != User.Role.ADMIN) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Json.createObjectBuilder()
                            .add("error", "Нет прав доступа").build())
                    .build();
        }
        return null;
    }
}
