package com.blog.demo.service;

import com.blog.demo.dto.CommentResponse;
import com.blog.demo.dto.NotificationDTO;
import com.blog.demo.dto.NotificationResponse;
import com.blog.demo.entity.Comment;
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

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final CacheManager cacheManager;

    private NotificationResponse toResponse(List<Notification> notifications) {

        List <NotificationDTO> notificationDTOS = new ArrayList<>();
        notifications.forEach(notification -> notificationDTOS.add(
                new NotificationDTO(notification)
        ));
        return new NotificationResponse(
                (long) notifications.size(),
                (long) notifications.size(),
                notificationDTOS);
    }

    @Override
    @Cacheable(value = "notifications", key = "#userId")
    public NotificationResponse getNotifications(int userId) {
        return toResponse(notificationRepository.findAllByReceiverId(userId));
    }

    @Override
    public Notification getNotification(int notificationId) {
        return notificationRepository.findById((long) notificationId)
                .orElseThrow();
    }


    private void cacheNotification(Notification notification) {
        int userId = notification.getReceiver().getId();
        Cache cache = cacheManager.getCache("notifications");

        if (cache != null) {
            NotificationResponse cached = cache.get(userId, NotificationResponse.class);

            if (cached != null) {
                List<NotificationDTO> updatedList = new ArrayList<>(cached.getNotifications());
                updatedList.addFirst(new NotificationDTO(notification));
//                if (updatedList.size() > 5)
//                    updatedList = new ArrayList<>(updatedList.subList(0, 5));

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
        notificationRepository.save(notification);
        cacheNotification(notification);
    }

    @Override
    @Transactional
    public void deleteNotification(int id) {
        notificationRepository.deleteById((long) id);
    }
}
