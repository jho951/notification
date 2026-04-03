package com.notification.core;

public final class NotificationResult {
    private final boolean success;
    private final String providerMessageId;
    private final String errorMessage;

    private NotificationResult(boolean success, String providerMessageId, String errorMessage) {
        this.success = success;
        this.providerMessageId = providerMessageId;
        this.errorMessage = errorMessage;
    }

    public static NotificationResult success(String providerMessageId) {
        return new NotificationResult(true, providerMessageId, null);
    }

    public static NotificationResult failure(String errorMessage) {
        return new NotificationResult(false, null, errorMessage == null ? "unknown error" : errorMessage);
    }

    public boolean success() { return success; }
    public String providerMessageId() { return providerMessageId; }
    public String errorMessage() { return errorMessage; }
}
