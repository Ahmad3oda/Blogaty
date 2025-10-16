package com.blog.demo.service;

import com.blog.demo.dto.NotificationDTO;
import com.blog.demo.dto.NotificationResponse;
import com.blog.demo.entity.Notification;
import com.blog.demo.exception.GlobalException;
import com.blog.demo.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    final NotificationRepository notificationRepository;
    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

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
    public NotificationResponse getNotifications(int userId) {
        return toResponse(notificationRepository.findAllByReceiverId(userId));
    }

    @Override
    public Notification getNotification(int notificationId) {
        return notificationRepository.findById((long) notificationId)
                .orElseThrow();
    }

    @Override
    @Transactional
    public void addNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void deleteNotification(int id) {
        notificationRepository.deleteById((long) id);
    }
}
