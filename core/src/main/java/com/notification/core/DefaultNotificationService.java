package com.notification.core;

import java.util.List;
import java.util.Map;

public final class DefaultNotificationService implements NotificationService {

    private final SenderRegistry registry;

    public DefaultNotificationService(SenderRegistry registry) {
        this.registry = registry;
    }

    @Override
    public NotificationResult send(ChannelId channelId, NotificationMessage message, List<Recipient> recipients, Map<String, String> attributes) {
        NotificationRequest req = new NotificationRequest(channelId, message, recipients, attributes);
        return registry.getOrThrow(channelId).send(req);
    }
}
