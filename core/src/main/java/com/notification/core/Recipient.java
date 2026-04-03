package com.notification.core;

import java.util.Objects;

public final class Recipient {
    private final String id;
    private final String address; // email, phone, webhook routing key 등 "채널이 해석"

    public Recipient(String id, String address) {
        if (address == null || address.isBlank()) throw new IllegalArgumentException("recipient address is blank");
        this.id = (id == null || id.isBlank()) ? null : id.trim();
        this.address = address.trim();
    }

    public String id() { return id; }
    public String address() { return address; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recipient)) return false;
        Recipient that = (Recipient) o;
        return Objects.equals(id, that.id) && address.equals(that.address);
    }

    @Override public int hashCode() { return Objects.hash(id, address); }
}
