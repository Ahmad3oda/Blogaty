package com.blog.demo.controller;

import com.blog.demo.config.SseConfig;
import com.blog.demo.dto.NotificationResponse;
import com.blog.demo.entity.Notification;
import com.blog.demo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final SseConfig sseConfig;

    @GetMapping(value = "/stream/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(@PathVariable int userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // No timeout
        sseConfig.addEmitter(userId, emitter);
        return emitter;
    }

    @Autowired
    public NotificationController(NotificationService notificationService, SseConfig sseConfig) {
        this.notificationService = notificationService;
        this.sseConfig = sseConfig;
    }
    // NEW: SSE endpoint - clients connect here for real-time updates

    @PostMapping("/test/{userId}")
    public void testNotification(@PathVariable int userId) {
        Notification testNotif = new Notification();
        testNotif.setMessage("Test notification!");
        testNotif.setCreatedAt(LocalDateTime.now());
        sseConfig.sendNotificationToUser(userId, testNotif);
    }

    @GetMapping("/user/{userId}")
    public NotificationResponse getNotifications(@PathVariable int userId) {
        return notificationService.getNotifications(userId);
    }

    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable int id) {
        notificationService.deleteNotification(id);
    }
}
