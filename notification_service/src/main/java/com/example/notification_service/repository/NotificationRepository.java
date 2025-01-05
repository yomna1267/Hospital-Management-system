package com.example.notification_service.repository;

import com.example.notification_service.entity.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notifications,Long> {
}
