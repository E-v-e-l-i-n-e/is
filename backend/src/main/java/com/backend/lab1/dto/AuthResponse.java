package com.backend.lab1.dto;

import com.backend.lab1.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private User.Role role;
    private Boolean isWaitingAdmin;
}
