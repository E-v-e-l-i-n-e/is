package com.backend.lab1.service;

import com.backend.lab1.repository.UserRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

@Stateless
public class UserService {
    @Inject
    UserRepository userRepository;
}
