package com.notification.core;

import java.util.Objects;

public final class ChannelId {
    private final String value;

    public ChannelId(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("channelId is blank");
        this.value = value.trim();
    }

    public String value() { return value; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChannelId)) return false;
        ChannelId that = (ChannelId) o;
        return value.equals(that.value);
    }

    @Override public int hashCode() { return Objects.hash(value); }

    @Override public String toString() { return value; }
}
