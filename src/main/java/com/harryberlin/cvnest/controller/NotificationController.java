package com.harryberlin.cvnest.controller;

import com.harryberlin.cvnest.domain.Notification;
import com.harryberlin.cvnest.dto.response.ApiResponse;
import com.harryberlin.cvnest.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PutMapping("{id}/read")
    public ApiResponse<String> markNotificationAsRead(@PathVariable("id") String id) {
        this.notificationService.markAsRead(id);
        return ApiResponse.<String>builder()
                .statusCode(200)
                .message("Notification marked as read")
                .data(null)
                .build();
    }

    @GetMapping("/{userId}")
    public ApiResponse<List<Notification>> getNotifications(@PathVariable("userId") String userId) {
        List<Notification> notifications = this.notificationService.getAllNotifications(userId);
        return ApiResponse.<List<Notification>>builder()
                .statusCode(200)
                .message("Get all notifications successfully")
                .data(notifications)
                .build();
    }
}
