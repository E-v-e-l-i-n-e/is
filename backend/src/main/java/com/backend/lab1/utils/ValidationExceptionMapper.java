package com.backend.lab1.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        Map<String, String> errors = new HashMap<>();
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();

        for (ConstraintViolation<?> violation : violations) {
            var fullField = violation.getPropertyPath().toString(); // Получение имени поля
            int firstDot = fullField.indexOf('.');
            int secondDot = fullField.indexOf('.', firstDot + 1);
            var field = fullField.substring(secondDot + 1);
            String[] parts = field.split("\\.");

            StringBuilder result = new StringBuilder(parts[0]);

            for (int i = 1; i < parts.length; i++) {
                String part = parts[i];
                result.append(Character.toUpperCase(part.charAt(0)));
                result.append(part.substring(1));
            }
            field = result.toString();

            String message = violation.getMessage();
            errors.put(field, message);
        }

        return Response.status(Response.Status.CONFLICT).entity(errors).build();
    }
}