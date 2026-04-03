package com.notification.core;

import java.util.Objects;

public final class NotificationMessage {
    private final String title;
    private final String body;

    public NotificationMessage(String title, String body) {
        if (body == null || body.isBlank()) throw new IllegalArgumentException("message body is blank");
        this.title = (title == null || title.isBlank()) ? null : title.trim();
        this.body = body.trim();
    }

    public String title() { return title; }
    public String body() { return body; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationMessage)) return false;
        NotificationMessage that = (NotificationMessage) o;
        return Objects.equals(title, that.title) && body.equals(that.body);
    }

    @Override public int hashCode() { return Objects.hash(title, body); }
}
