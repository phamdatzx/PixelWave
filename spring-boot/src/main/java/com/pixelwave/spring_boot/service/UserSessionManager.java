package com.pixelwave.spring_boot.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class UserSessionManager {

    private final Map<String, String> userSessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionUsers = new ConcurrentHashMap<>();

    public void addUserSession(String userId, String sessionId) {
        userSessions.put(userId, sessionId);
        sessionUsers.put(sessionId, userId);
        System.out.println("User session added: " + userId + " -> " + sessionId);
    }

    public void removeUserSession(String userId) {
        String sessionId = userSessions.remove(userId);
        if (sessionId != null) {
            sessionUsers.remove(sessionId);
            System.out.println("User session removed: " + userId);
        }
    }

    public void removeSessionUser(String sessionId) {
        String userId = sessionUsers.remove(sessionId);
        if (userId != null) {
            userSessions.remove(userId);
            System.out.println("Session removed: " + sessionId + " for user: " + userId);
        }
    }

    public boolean isUserConnected(String userId) {
        return userSessions.containsKey(userId);
    }

    public String getSessionIdByUserId(String userId) {
        return userSessions.get(userId);
    }

    public String getUserIdBySessionId(String sessionId) {
        return sessionUsers.get(sessionId);
    }

    // Additional method to get all sessions for debugging
    public void printAllSessions() {
        System.out.println("=== All User Sessions ===");
        userSessions.forEach((userId, sessionId) ->
                System.out.println("User: " + userId + " -> Session: " + sessionId));
        System.out.println("========================");
    }
}
