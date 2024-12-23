package com.backend.lab1.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "login", nullable = false, unique = true)
    @NotNull
    @NotBlank
    private String login;

    @Column(name = "password", nullable = false)
    @NotNull
    @NotBlank
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(name = "isWaitingAdmin")
    private Boolean isWaitingAdmin;

    public enum Role {
        ADMIN,
        USER
    }
}




