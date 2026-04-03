package com.notification.api;

import java.util.List;
import java.util.Map;

public final class SendNotificationCommand {
    public String channelId;          // "console", "webhook" ...
    public String title;              // optional
    public String body;               // required
    public List<String> recipients;   // addresses
    public Map<String, String> attributes;
}
