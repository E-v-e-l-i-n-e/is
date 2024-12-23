package com.backend.lab1.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "car")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Car {

    @Id
    @Column(name="name",nullable = false, unique = true)
    @NotNull (message = "Машина не может быть пустой")
    @NotBlank (message = "Машина не может быть пустой")
    private String name;
}
