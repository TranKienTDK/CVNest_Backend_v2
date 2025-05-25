package com.harryberlin.cvnest.domain;

import com.harryberlin.cvnest.util.constant.NotificationStatusEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private String id;

    private String receiveId;
    private String applyId;
    private String content;
    private NotificationStatusEnum status;
    private LocalDateTime createdAt;
}
