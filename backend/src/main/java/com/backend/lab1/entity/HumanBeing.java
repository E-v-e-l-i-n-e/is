package com.backend.lab1.entity;


import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "human_being")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HumanBeing {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    @NotNull(message = "Имя не может быть пустым")
    @NotBlank(message = "Имя не может состоять из пробелов")
    private String name;

    @Embedded
    @Valid
    @NotNull (message = "Координаты не могут быть пустыми")
    @Column(name="coordinates", nullable = false)
    private Coordinates coordinates;

    @Column(name="creation_date", nullable = false)
    private java.time.LocalDateTime creationDate;


    @Column(name = "real_hero", nullable = false)
    @NotNull(message = "Поле real hero не может быть пустыми")
    private Boolean realHero;

    @NotNull(message = "Поле has toothpick не может быть пустым")
    @Column(name = "has_toothpick", nullable = false)
    private Boolean hasToothpick;

    @Valid
    @ManyToOne(optional = true)
    @JoinColumn(name = "car_name", nullable = true)
    private Car car;


    @Enumerated(EnumType.STRING)
    @NotNull(message = "Настроение не может быть пустым")
    @Column(name = "mood", nullable = false)
    private HumanBeing.Mood mood;

    @NotNull (message = "Скорость удара не может быть пустой")
    @Column(name = "impact_speed", nullable = false)
    @Max(value = 185, message = "Скорость удара не может быть больше 185")
    private Float impactSpeed;

    @NotNull (message = "Время ожидания не может быть пустым")
    @Column(name = "minutes_of_waiting", nullable = false)
    private Integer minutesOfWaiting;

    @Enumerated(EnumType.STRING)
    @NotNull (message = "Время ожидания не может быть пустым")
    @Column(name = "weapon_type", nullable = false)
    private HumanBeing.WeaponType weaponType;

    @ManyToOne
    @JoinColumn(name = "creator_login", nullable = false)
    private User user;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Coordinates {
        @NotNull (message = "Координата Х не может быть пустой")
        @Max(value = 712, message = "Значение координаты Х не может быть больше 712")
        private Long x;
        private long y;
    }
    public enum Mood {
        SORROW,
        LONGING,
        GLOOM,
        FRENZY;
    }
    public enum WeaponType {
        HAMMER,
        PISTOL,
        SHOTGUN;
    }
}
