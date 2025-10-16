package com.blog.demo.dto;

import com.blog.demo.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    Long notificationsCount;
    Long unreadNotifications;
    List<NotificationDTO> notifications;
}
