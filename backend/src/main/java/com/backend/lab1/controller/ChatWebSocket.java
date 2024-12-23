package com.backend.lab1.controller;

import com.backend.lab1.dto.AuthResponse;
import com.backend.lab1.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/chat")
public class ChatWebSocket {

    @Inject
    JwtService jwtService;

    private static final ConcurrentHashMap<String, Session> userSessions = new ConcurrentHashMap<>();
    private static final CopyOnWriteArraySet<Session> allSessions = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        String token = session.getRequestParameterMap().get("token").get(0);
        String login = jwtService.extractUserName(token);
        userSessions.put(login, session);
        allSessions.add(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received message: " + message);
    }

    @OnClose
    public void onClose(Session session) {
        userSessions.values().remove(session);
        allSessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
    }

    public static void sendPersonalMessage(String login, AuthResponse message) {
        Session session = userSessions.get(login);
        if (session != null && session.isOpen()) {
            try {
                var objectMapper = new ObjectMapper();
                String jsonMessage = objectMapper.writeValueAsString(message);
                session.getBasicRemote().sendText(jsonMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendBroadcastMessage(String message) {
        allSessions.forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}