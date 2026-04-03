package com.notification.api;

public interface NotificationClient {
    SendNotificationResponse send(SendNotificationCommand command);
}
