package com.backend.lab1.controller;

import com.backend.lab1.dto.AcceptAdminRequest;
import com.backend.lab1.dto.AuthResponse;
import com.backend.lab1.repository.UserRepository;
import com.backend.lab1.service.JwtService;
import jakarta.ejb.EJB;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.HttpHeaders;
import com.backend.lab1.entity.User;
import jakarta.ws.rs.core.Context;
import  com.backend.lab1.service.AdminService;

import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

@Path("/admin")
public class AdminController {
    @EJB
    UserRepository userRepository;
    @EJB
    private JwtService jwtService;
    @EJB
    private AdminService adminService;
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/waiting-admin")
    public Response getWaitingAdminList(@Context HttpHeaders headers) {
        String authorizationHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        Response checkAdmin = adminService.checkAdminByToken(authorizationHeader);
        if (checkAdmin != null) {
            return checkAdmin;
        }
        List<User> usersWaitingAdmin = userRepository.findIsWaitingAdmin();
        var usersLogins = usersWaitingAdmin.stream().map((User::getLogin)).collect(Collectors.toList());
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        for (String userName : usersLogins) {
            arrayBuilder.add(userName);
        }
        JsonObject jsonResponse = Json.createObjectBuilder()
                .add("logins", arrayBuilder)
                .build();

        return Response.ok()
                .entity(jsonResponse)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/accept-admin")
    public Response acceptAdministrator(@Context HttpHeaders headers, AcceptAdminRequest acceptAdminRequest) {
        String authorizationHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        Response checkAdmin = adminService.checkAdminByToken(authorizationHeader);
        if (checkAdmin != null) {
            return checkAdmin;
        }
        User user = userRepository.findByLogin(acceptAdminRequest.getLogin());
        if (user == null || user.getRole() == User.Role.ADMIN) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Json.createObjectBuilder()
                            .add("error", "Пользователь не найден").build())
                    .build();
        }
        if (!user.getIsWaitingAdmin()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Json.createObjectBuilder()
                            .add("error", "Пользователь не хочет быть администратором").build())
                    .build();
        }
        user.setRole(User.Role.ADMIN);
        user.setIsWaitingAdmin(Boolean.FALSE);
        userRepository.update(user);

        var websocketResponse = new AuthResponse(
                jwtService.generateToken(user),
                user.getRole(),
                user.getIsWaitingAdmin()
        );
        ChatWebSocket.sendPersonalMessage(user.getLogin(), websocketResponse);
        return Response.ok().build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/reject-admin")
    public Response rejectAdministrator(@Context HttpHeaders headers, AcceptAdminRequest acceptAdminRequest) {
        String authorizationHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        Response checkAdmin = adminService.checkAdminByToken(authorizationHeader);
        if (checkAdmin != null) {
            return checkAdmin;
        }
        User user = userRepository.findByLogin(acceptAdminRequest.getLogin());
        if (user == null || user.getRole() == User.Role.ADMIN) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Json.createObjectBuilder()
                            .add("error", "Пользователь не найден").build())
                    .build();
        }
        if (!user.getIsWaitingAdmin()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Json.createObjectBuilder()
                            .add("error", "Пользователь не подавал зявку").build())
                    .build();
        }
        user.setIsWaitingAdmin(Boolean.FALSE);
        userRepository.update(user);

        var websocketResponse = new AuthResponse(
                jwtService.generateToken(user),
                user.getRole(),
                user.getIsWaitingAdmin()
        );
        ChatWebSocket.sendPersonalMessage(user.getLogin(), websocketResponse);

        return Response.ok().build();
    }
}

