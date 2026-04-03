package com.notification.core.senders;

import com.notification.core.*;

import java.time.Instant;

public final class ConsoleNotificationSender implements NotificationSender {

    private final ChannelId channelId = new ChannelId("console");

    @Override
    public ChannelId channelId() {
        return channelId;
    }

    @Override
    public NotificationResult send(NotificationRequest request) {
        try {
            System.out.println("=== NOTIFICATION(console) @ " + Instant.now() + " ===");
            System.out.println("title: " + request.message().title());
            System.out.println("body : " + request.message().body());
            System.out.println("to   : " + request.recipients().size());
            System.out.println("attr : " + request.attributes());
            System.out.println("======================================");
            return NotificationResult.success("console-" + System.nanoTime());
        } catch (Exception e) {
            return NotificationResult.failure(e.getMessage());
        }
    }
}
