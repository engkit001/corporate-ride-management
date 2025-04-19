package com.in6225.crms.notifservice.service;

import jakarta.annotation.security.DenyAll;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotifKafkaListener {
    private final NotifService notifService;

    @KafkaListener(topics = "driver-assigned", groupId = "notif-service-group")
    public void handleDriverAssignedEvent(String message) {
        notifService.sendNotif(message);
    }
}
