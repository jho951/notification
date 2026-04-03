package com.notification.core;

import java.util.List;
import java.util.Map;

public interface NotificationService {
    NotificationResult send(ChannelId channelId, NotificationMessage message, List<Recipient> recipients, Map<String, String> attributes);
}
