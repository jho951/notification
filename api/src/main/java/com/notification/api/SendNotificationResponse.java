package com.notification.api;

public final class SendNotificationResponse {
    public boolean success;
    public String providerMessageId;
    public String errorMessage;

    public static SendNotificationResponse ok(String id) {
        SendNotificationResponse r = new SendNotificationResponse();
        r.success = true;
        r.providerMessageId = id;
        return r;
    }

    public static SendNotificationResponse fail(String msg) {
        SendNotificationResponse r = new SendNotificationResponse();
        r.success = false;
        r.errorMessage = msg;
        return r;
    }
}
