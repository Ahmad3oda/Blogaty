package com.blog.demo.controller;

import com.blog.demo.dto.NotificationResponse;
import com.blog.demo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
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
