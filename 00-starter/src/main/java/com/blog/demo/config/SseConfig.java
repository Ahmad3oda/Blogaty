package com.blog.demo.config;

import com.blog.demo.entity.Notification;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class SseConfig {

    // Store all connected clients (userId -> their SSE emitter)
    private final Map<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();

    // When a user connects, save their emitter
    public void addEmitter(int userId, SseEmitter emitter) {
        emitters.put(userId, emitter);

        // Clean up when connection closes or times out
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((e) -> emitters.remove(userId));
    }

    // Send notification to specific user
    public void sendNotificationToUser(int userId, Notification notification) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")  // Event name
                        .data(notification));   // Your notification object
            } catch (IOException e) {
                emitters.remove(userId);  // Remove if sending fails
            }
        }
    }

    // Get all connected user IDs (useful for debugging)
    public Set<Integer> getConnectedUsers() {
        return emitters.keySet();
    }
}