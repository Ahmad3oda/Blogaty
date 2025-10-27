package com.blog.demo.repository;

import com.blog.demo.entity.Notification;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.receiver.id = :receiverId ORDER BY n.createdAt DESC")
    List<Notification> findAllByReceiverId(@Param("receiverId") int receiverId);

}
