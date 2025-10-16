package com.blog.demo.dto;

import com.blog.demo.entity.Notification;
import com.blog.demo.entity.NotificationType;
import com.blog.demo.entity.TargetType;
import com.blog.demo.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private UserResponse receiver;
    private UserResponse actor;
    private NotificationType type;

    private Long targetId;
    private TargetType targetType;
    private String message;
    private LocalDateTime createdAt = LocalDateTime.now();
    private boolean isRead = false;

    public NotificationDTO(Notification notification) {
        this.actor = new UserResponse(notification.getActor());
        this.receiver = new UserResponse(notification.getReceiver());
        this.type = notification.getType();
        this.targetId = notification.getTargetId();
        this.targetType = notification.getTargetType();
        this.message = notification.getMessage();
        this.createdAt = notification.getCreatedAt();
        this.isRead = notification.isRead();
    }
}
