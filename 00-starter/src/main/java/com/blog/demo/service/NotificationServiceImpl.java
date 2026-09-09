package com.blog.demo.service;

import com.blog.demo.config.SseConfig;
import com.blog.demo.dto.NotificationDTO;
import com.blog.demo.dto.NotificationResponse;
import com.blog.demo.entity.Notification;
import com.blog.demo.exception.GlobalException;
import com.blog.demo.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final CacheManager cacheManager;
    private final SseConfig sseConfig;

    private NotificationResponse toResponse(List<Notification> notifications) {
        List<NotificationDTO> notificationDTOS = new ArrayList<>();
        notifications.forEach(notification -> notificationDTOS.add(
                new NotificationDTO(notification)
        ));
        long unreadCount = notifications.stream().filter(n -> !n.isRead()).count();
        return new NotificationResponse(
                (long) notifications.size(),
                unreadCount,
                notificationDTOS);
    }

    @Override
    @Cacheable(value = "notifications", key = "#userId")
    public NotificationResponse getNotifications(int userId) {
        return toResponse(notificationRepository.findAllByReceiverId((long) userId));
    }

    @Override
    public Notification getNotification(int notificationId) {
        return notificationRepository.findById((long) notificationId)
                .orElseThrow(() -> new GlobalException("Notification not found - id: " + notificationId));
    }

    private void cacheNotification(Notification notification) {
        int userId = Math.toIntExact(notification.getReceiver().getId());
        Cache cache = cacheManager.getCache("notifications");

        if (cache != null) {
            NotificationResponse cached = cache.get(userId, NotificationResponse.class);

            if (cached != null) {
                List<NotificationDTO> updatedList = new ArrayList<>(cached.getNotifications());
                updatedList.addFirst(new NotificationDTO(notification));

                long totalCount = cached.getNotificationsCount() + 1;
                long unreadCount = cached.getUnreadNotifications() + 1;

                NotificationResponse updatedResponse = new NotificationResponse(
                        totalCount,
                        unreadCount,
                        updatedList
                );
                cache.put(userId, updatedResponse);
            }
        }
    }

    @Override
    @Transactional
    public void addNotification(Notification notification) {
        if (!Objects.equals(notification.getReceiver().getId(), notification.getActor().getId())) {
            notificationRepository.save(notification);
            cacheNotification(notification);
            sseConfig.sendNotificationToUser(Math.toIntExact(notification.getReceiver().getId()), notification);
        }
    }

    @Override
    @Transactional
    public void deleteNotification(int id) {
        notificationRepository.deleteById((long) id);
    }
}
