package com.backend.lab1.dto;

import com.backend.lab1.entity.Car;
import com.backend.lab1.entity.HumanBeing;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HumanBeingRequest {

    Long id;

    @NotNull(message = "Имя не может быть пустым")
    @NotBlank(message = "Имя не может быть пустым")
    String name;

    @NotNull(message = "Координаты не могут быть пустыми")
    @Valid
    HumanBeing.Coordinates coordinates;

    @Valid
    Car car;

    @NotNull(message = "Поле real hero не может быть пустыми")
    Boolean realHero;

    @NotNull(message = "Поле has toothpick не может быть пустым")
    Boolean hasToothpick;

    @NotNull(message = "Настроение не может быть пустым")
    HumanBeing.Mood mood;

    @NotNull(message = "Тип оружия не может быть пустым")
    HumanBeing.WeaponType weaponType;

    @Max(value = 185, message = "Скорость удара не может быть больше 185")
    @NotNull (message = "Скорость удара не может быть пустой")
    Float impactSpeed;

    @NotNull (message = "Время ожидания не может быть пустым")
    Integer minutesOfWaiting;
}
