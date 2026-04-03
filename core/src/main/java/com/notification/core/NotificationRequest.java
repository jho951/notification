package com.notification.core;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class NotificationRequest {
    private final ChannelId channelId;
    private final NotificationMessage message;
    private final List<Recipient> recipients;
    private final Map<String, String> attributes;

    public NotificationRequest(
            ChannelId channelId,
            NotificationMessage message,
            List<Recipient> recipients,
            Map<String, String> attributes
    ) {
        if (channelId == null) throw new IllegalArgumentException("channelId is null");
        if (message == null) throw new IllegalArgumentException("message is null");
        if (recipients == null || recipients.isEmpty()) throw new IllegalArgumentException("recipients is empty");
        this.channelId = channelId;
        this.message = message;
        this.recipients = List.copyOf(recipients);
        this.attributes = attributes == null ? Collections.emptyMap() : Map.copyOf(attributes);
    }

    public ChannelId channelId() { return channelId; }
    public NotificationMessage message() { return message; }
    public List<Recipient> recipients() { return recipients; }
    public Map<String, String> attributes() { return attributes; }
}
