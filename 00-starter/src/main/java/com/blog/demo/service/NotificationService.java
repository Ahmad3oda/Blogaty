package com.blog.demo.service;

import com.blog.demo.dto.NotificationResponse;
import com.blog.demo.entity.Notification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public interface NotificationService {
    NotificationResponse getNotifications(int userId);
    Notification getNotification(int notificationId);
    void addNotification(Notification notification);
    void deleteNotification(int id);
}
