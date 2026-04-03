package com.notification.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SenderRegistry {
    private final Map<ChannelId, NotificationSender> senders = new HashMap<>();

    public SenderRegistry(List<NotificationSender> senders) {
        if (senders != null) {
            for (NotificationSender s : senders) {
                this.senders.put(s.channelId(), s);
            }
        }
    }

    public NotificationSender getOrThrow(ChannelId channelId) {
        NotificationSender sender = senders.get(channelId);
        if (sender == null) throw new IllegalStateException("No sender for channelId=" + channelId);
        return sender;
    }
}
