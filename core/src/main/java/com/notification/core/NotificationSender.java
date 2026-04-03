package com.notification.core;

public interface NotificationSender {
    ChannelId channelId();
    NotificationResult send(NotificationRequest request);
}
