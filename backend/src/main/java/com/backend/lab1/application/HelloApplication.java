package com.backend.lab1.application;

import com.backend.lab1.controller.AdminController;
import com.backend.lab1.controller.AuthController;
import com.backend.lab1.controller.HumanBeingController;
import com.backend.lab1.utils.CORSFilter;
import com.backend.lab1.utils.ValidationExceptionMapper;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.Set;

@ApplicationPath("")
public class HelloApplication extends Application {
//    @Override
//    public Set<Class<?>> getClasses() {
//        return Set.of(
//                HumanBeingController.class,
//                AuthController.class,
//                AdminController.class,
//                ValidationExceptionMapper.class,
//                CORSFilter.class
//                );
//    }
}