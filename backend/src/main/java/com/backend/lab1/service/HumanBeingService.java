package com.backend.lab1.service;

import com.backend.lab1.entity.User;
import com.backend.lab1.repository.UserRepository;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.json.Json;
import jakarta.ws.rs.core.Response;

@Stateless
public class HumanBeingService {
    @EJB
    private JwtService jwtService;
    @EJB
    UserRepository userRepository;
    public Response checkToken(String authorizationHeader) {
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
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Json.createObjectBuilder()
                            .add("error", "Пользователь не найден").build())
                    .build();
        }

        return null;
    }
}
