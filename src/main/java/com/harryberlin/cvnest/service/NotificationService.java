package com.harryberlin.cvnest.service;

import com.harryberlin.cvnest.domain.Notification;
import com.harryberlin.cvnest.repository.NotificationRepository;
import com.harryberlin.cvnest.util.constant.NotificationStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Transactional
    public void markAsRead(String notificationId) {
        this.notificationRepository.updateStatusToRead(notificationId, NotificationStatusEnum.READ);
    }

    public List<Notification> getAllNotifications(String receiverId) {
        return this.notificationRepository.findByReceiveIdOrderByCreatedAtDesc(receiverId);
    }
}
