package com.backend.lab1.utils;

import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.HttpHeaders;

@Provider
public class CORSFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext, jakarta.ws.rs.container.ContainerResponseContext responseContext) {
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}
