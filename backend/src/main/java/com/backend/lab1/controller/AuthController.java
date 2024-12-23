package com.backend.lab1.controller;

import com.backend.lab1.dto.AuthResponse;
import com.backend.lab1.repository.UserRepository;
import com.backend.lab1.service.JwtService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.json.Json;
import com.backend.lab1.entity.User;
import org.hibernate.boot.jaxb.SourceType;
import static com.backend.lab1.utils.PasswordEncoding.encodePassword;

@Path("/auth")
public class AuthController {

    @EJB
    UserRepository userRepository;

    @EJB
    JwtService jwtService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/sign-up")
    public Response signUp(User user) {
        String errorMessage = inputValidation(user);
        if (errorMessage != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(Json.createObjectBuilder().add("error", errorMessage).build())
                    .build();
        }
        var currentUser = userRepository.findByLogin(user.getLogin());
        if (currentUser == null){
            String error = "Пользователя с таким логином не существует";
            return Response.status(Response.Status.NOT_FOUND).
                    entity(Json.createObjectBuilder().add("error", error).build()).build();
        }
        if (!encodePassword(user.getPassword()).equals(userRepository.findByLogin(user.getLogin()).getPassword())){
            String error = "Неправильный пароль";
            return Response.status(Response.Status.NOT_FOUND).
                    entity(Json.createObjectBuilder().add("error", error).build()).build();
        }

        var token = jwtService.generateToken(currentUser);
        return Response.ok(new AuthResponse(token, currentUser.getRole(), currentUser.getIsWaitingAdmin())).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/sign-in")
    public Response signIn(User user) {

        String errorMessage = inputValidation(user);
        if (errorMessage != null){
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(Json.createObjectBuilder().add("error", errorMessage).build())
                    .build();
        }
        if (userRepository.findByLogin(user.getLogin()) != null){
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(Json.createObjectBuilder().add("error", "Это имя занято").build()).build();
        }
        if (user.getIsWaitingAdmin()) {
            if (userRepository.findAdmin() == null ) {
                user.setRole(User.Role.ADMIN);
                user.setIsWaitingAdmin(false);
            } else {
                user.setRole(User.Role.USER);
            }
        } else {
            user.setRole(User.Role.USER);
        }
        String newPassword = encodePassword(user.getPassword());
        user.setPassword(newPassword);
        userRepository.save(user);

        var token = jwtService.generateToken(user);
        return Response.ok(new AuthResponse(token, user.getRole(), user.getIsWaitingAdmin())).build();
    }




    public String inputValidation(User user){
        String login = user.getLogin();
        String password = user.getPassword();
        String regex = ".*[a-zA-Zа-яА-Я].*";
        if (login.length() < 4 ){
            return "Длина логина < 4";
        } else if (!login.matches(regex)){
            return "Логин не содержит букв";
        } else if (login.length() > 15){
            return "Длина логина больше 15";
        } else if (password.length() < 5){
            return "Длина пароля меньше 5";
        }
        return null;
    }
}
