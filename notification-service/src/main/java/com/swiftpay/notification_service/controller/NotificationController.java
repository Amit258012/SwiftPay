package com.swiftpay.notification_service.controller;

import com.swiftpay.notification_service.entity.Notification;
import com.swiftpay.notification_service.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public Notification sendNotification(@RequestBody Notification notification){
        return notificationService.sendNotification(notification);
    }

    @GetMapping("/{userId}")
    public List<Notification> getNotificationByUser(@PathVariable String userId){
        return notificationService.getNotificatioinsByUserId(userId);
    }
}
