package com.backend.lab1.controller;

import com.backend.lab1.dto.AuthResponse;
import com.backend.lab1.dto.DeleteRequest;
import com.backend.lab1.dto.FiltersRequest;
import com.backend.lab1.dto.HumanBeingRequest;
import com.backend.lab1.entity.Car;
import com.backend.lab1.entity.HistoryChange;
import com.backend.lab1.entity.HumanBeing;
import com.backend.lab1.entity.User;
import com.backend.lab1.repository.CarRepository;
import com.backend.lab1.repository.HistoryChangeRepository;
import com.backend.lab1.repository.HumanBeingRepository;
import com.backend.lab1.repository.UserRepository;
import com.backend.lab1.service.HumanBeingService;
import com.backend.lab1.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ejb.EJB;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Path("/human")
public class HumanBeingController {
    @EJB
    UserRepository userRepository;
    @EJB
    JwtService jwtService;
    @EJB
    private HumanBeingService humanBeingService;
    @EJB
    private HumanBeingRepository humanBeingRepository;
    @EJB
    CarRepository carRepository;

    @EJB
    HistoryChangeRepository historyChangeRepository;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/create")
    public Response createHumanBeing(@Context HttpHeaders headers, @Valid HumanBeingRequest humanBeingRequest) {
        String authorizationHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        Response checkToken = humanBeingService.checkToken(authorizationHeader);
        if (checkToken != null) {
            return checkToken;
        }
        String login;
        String token = authorizationHeader.substring("Bearer ".length()).trim();
        login = jwtService.extractUserName(token);
        User user = userRepository.findByLogin(login);
        Car car = humanBeingRequest.getCar();
        if (car != null && !car.getName().isEmpty()) {
            if (!carRepository.isExistsCar(car.getName())) {
                carRepository.addCar(car);
            }
            car = carRepository.findByName(car.getName());
        }

        HumanBeing humanBeing = HumanBeing.builder().
                name(humanBeingRequest.getName())
                .coordinates(new HumanBeing.Coordinates(humanBeingRequest.getCoordinates().getX(), humanBeingRequest.getCoordinates().getY()))
                .creationDate(java.time.LocalDateTime.now())
                .realHero(humanBeingRequest.getRealHero())
                .hasToothpick(humanBeingRequest.getHasToothpick())
                .car(car)
                .mood(humanBeingRequest.getMood())
                .impactSpeed(humanBeingRequest.getImpactSpeed())
                .minutesOfWaiting(humanBeingRequest.getMinutesOfWaiting())
                .weaponType(humanBeingRequest.getWeaponType())
                .user(user)
                .build();

        humanBeingRepository.save(humanBeing);

        UpdateHumanBeingWebsocket.sendBroadcastMessage("Был добавлен человек");
        return Response.status(Response.Status.CREATED).build();
        }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/update")
    public Response updateHumanBeing(@Context HttpHeaders headers, @Valid HumanBeingRequest humanBeingRequest) {
        String authorizationHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        Response checkToken = humanBeingService.checkToken(authorizationHeader);
        if (checkToken != null) {
            return checkToken;
        }
        String login;
        String token = authorizationHeader.substring("Bearer ".length()).trim();
        login = jwtService.extractUserName(token);
        User user = userRepository.findByLogin(login);
        Car car = humanBeingRequest.getCar();
        if (humanBeingRequest.getId() == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Json.createObjectBuilder()
                            .add("error", "Не удалось найти элемент").build())
                    .build();
        }
        HumanBeing humanBeing = humanBeingRepository.findById(humanBeingRequest.getId());
        if (!humanBeing.getUser().getLogin().equals(login)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Json.createObjectBuilder()
                            .add("error", "Нет прав доступа").build())
                    .build();
        }
        Car car_before = humanBeing.getCar();
        if (car != null && !car.getName().isEmpty()) {
            if (!carRepository.isExistsCar(car.getName())) {
                carRepository.addCar(car);
            }
        }
        humanBeing.setName(humanBeing.getName());
        humanBeing.setCoordinates(humanBeingRequest.getCoordinates());
        humanBeing.setCar(car);
        humanBeing.setRealHero(humanBeingRequest.getRealHero());
        humanBeing.setHasToothpick(humanBeingRequest.getHasToothpick());
        humanBeing.setMood(humanBeingRequest.getMood());
        humanBeing.setWeaponType(humanBeingRequest.getWeaponType());
        humanBeing.setImpactSpeed(humanBeingRequest.getImpactSpeed());
        humanBeing.setMinutesOfWaiting(humanBeingRequest.getMinutesOfWaiting());

        humanBeingRepository.update(humanBeing);

        if (car_before != null && !carRepository.isCarInHumanBeing(car_before.getName())) {
            carRepository.deleteCar(car_before.getName());
        }

        HistoryChange change = HistoryChange.builder()
                .login(login)
                .humanBeingId(humanBeing.getId())
                .updatedAt(java.time.LocalDateTime.now())
                .build();
        historyChangeRepository.addChange(change);
        UpdateHumanBeingWebsocket.sendBroadcastMessage("Был обновлён человек");
        return Response.ok().build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes (MediaType.APPLICATION_JSON)
    @Path("/delete")
    public Response deleteHumanBeing(@Context HttpHeaders headers, DeleteRequest deleteRequest) {
        String authorizationHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        Response checkToken = humanBeingService.checkToken(authorizationHeader);
        if (checkToken != null) {
            return checkToken;
        }
        String login;
        String token = authorizationHeader.substring("Bearer ".length()).trim();
        login = jwtService.extractUserName(token);
        User user = userRepository.findByLogin(login);
        HumanBeing humanBeing = humanBeingRepository.findById(deleteRequest.getId());
        if (humanBeing == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Json.createObjectBuilder()
                            .add("error", "Объект не найден").build())
                    .build();
        }
        User creator = humanBeing.getUser();
        String creator_login = creator.getLogin();
        if (!user.getRole().equals(User.Role.ADMIN) || !login.equals(creator_login)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Json.createObjectBuilder()
                    .add("error", "Вы не можете удалить этот объект").build())
                    .build();
        }
        Car car = humanBeing.getCar();
        Boolean isSingleCarUsage = false;
        if (car != null) {
            isSingleCarUsage = carRepository.isOneCar(car.getName());
        }
        int deletedHumans = humanBeingRepository.deleteHumanBeing(deleteRequest.getId());
        if (deletedHumans == 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Json.createObjectBuilder()
                            .add("error", "Не удалось удалить объект").build())
                    .build();
        }
        if (car != null && isSingleCarUsage) {
            int deletedCars = carRepository.deleteCar(car.getName());
            if (deletedCars == 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Json.createObjectBuilder()
                                .add("error", "Не удалось удалить машину").build())
                        .build();
            }
        }

        historyChangeRepository.deleteChange(deleteRequest.getId());
        UpdateHumanBeingWebsocket.sendBroadcastMessage("Был удалён человек");
        return Response.ok().build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list")
    public Response getHumanBeings(@QueryParam("page") int page, @QueryParam("size") int size) {
        List<HumanBeing> humanBeings = humanBeingRepository.findPaginated(page, size);
        int total = humanBeingRepository.count();
        Map<String, Object> response = new HashMap<>();
        response.put("data", humanBeings);
        response.put("total", total);
        response.put("page", page);
        response.put("size", size);
        return Response.ok(response).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/filtered-list")
    public Response getHumanBeingsWithFilters(FiltersRequest request) {
        List<HumanBeing> humanBeings = humanBeingRepository.findFilteredPaginated(request);
        int total = humanBeingRepository.count();
        Map<String, Object> response = new HashMap<>();
        response.put("data", humanBeings);
        response.put("total", total);
        response.put("page", request.getPage());
        response.put("size", request.getSize());
        return Response.ok(response).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/cars")
    public Response getHumanBeings(@Context HttpHeaders headers) {
        String authorizationHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        Response checkToken = humanBeingService.checkToken(authorizationHeader);
        if (checkToken != null) {
            return checkToken;
        }
        List<Car> cars = carRepository.getAllCars();
        var listOfCars =  cars.stream().map((Car::getName)).collect(Collectors.toList());
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        for (String carName : listOfCars) {
            jsonArrayBuilder.add(carName);
        }

        JsonObject jsonResponse = Json.createObjectBuilder()
                .add("cars", jsonArrayBuilder)
                .build();
        return Response.ok()
                .entity(jsonResponse)
                .build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/avgImpactSpeed")
    public Response averageImpactSpeed() {
        var avgImpactSpeed = humanBeingRepository.averageImpactSpeed();
        return Response.ok().entity(Json.createObjectBuilder()
                        .add("avgImpactSpeed", avgImpactSpeed != null ? avgImpactSpeed : 0).build())
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/waiting_minutes_less")
    public Response waitingMinutesLess(Map<String, Integer> w) {
        Integer waitingMinutes = w.get("waitingMinutes");
        var waitingMinutesLess = humanBeingRepository.countWaitingMinutesLessThan(waitingMinutes);
        return Response.ok().entity(Json.createObjectBuilder()
                        .add("waitingMinutesLess", waitingMinutesLess != null ? waitingMinutesLess : 0).build())
                .build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/update_car_name")
    public Response updateCarName() {
        humanBeingRepository.updateCarName();
        UpdateHumanBeingWebsocket.sendBroadcastMessage("Были обновлены имена машин");
        return Response.ok().build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/delete_non_toothpick")
    public Response deleteNonToothpick() {
        humanBeingRepository.delteNonToothpick();
        UpdateHumanBeingWebsocket.sendBroadcastMessage("Были обновлены имена машин");
        return Response.ok().build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/name_start_from")
    public Response getHumanBeingsWithFilters(Map<String, String> request) {
        var prefix = request.get("prefix");
        List<HumanBeing> humanBeings = humanBeingRepository.getByPrefix(prefix);
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("data", humanBeings);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/get_by_id")
    public Response getById(Map<String, Long> request) {

        var id = request.get("id");
        HumanBeing humanBeings = humanBeingRepository.findById(id);
        return Response.ok(humanBeings).build();
    }
}
