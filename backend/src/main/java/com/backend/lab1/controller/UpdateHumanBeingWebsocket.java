package com.backend.lab1.controller;

import com.backend.lab1.service.JwtService;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/update-humans")
public class UpdateHumanBeingWebsocket {
    @Inject
    JwtService jwtService;

    private static final ConcurrentHashMap<String, Session> userSessions = new ConcurrentHashMap<>();
    private static final CopyOnWriteArraySet<Session> allSessions = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        allSessions.add(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received message: " + message);
    }

    @OnClose
    public void onClose(Session session) {
        allSessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
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