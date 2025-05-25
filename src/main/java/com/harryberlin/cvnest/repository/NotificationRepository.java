package com.harryberlin.cvnest.repository;

import com.harryberlin.cvnest.domain.Notification;
import com.harryberlin.cvnest.util.constant.NotificationStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    @Modifying
    @Query("UPDATE notifications n SET n.status = :status WHERE n.id = :id")
    void updateStatusToRead(String id, NotificationStatusEnum status);

    List<Notification> findByReceiveIdOrderByCreatedAtDesc(String receiveId);
}
