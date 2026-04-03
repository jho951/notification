package com.notification.core.senders;

import com.notification.core.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public final class WebhookNotificationSender implements NotificationSender {

    private final ChannelId channelId = new ChannelId("webhook");
    private final HttpClient httpClient;
    private final URI webhookUrl;

    public WebhookNotificationSender(String webhookUrl) {
        if (webhookUrl == null || webhookUrl.isBlank()) throw new IllegalArgumentException("webhookUrl is blank");
        this.webhookUrl = URI.create(webhookUrl.trim());
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public ChannelId channelId() {
        return channelId;
    }

    @Override
    public NotificationResult send(NotificationRequest request) {
        try {
            String json = toJson(request);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(webhookUrl)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> resp = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                return NotificationResult.success("webhook-" + System.nanoTime());
            }
            return NotificationResult.failure("webhook status=" + resp.statusCode() + ", body=" + safe(resp.body()));
        } catch (Exception e) {
            return NotificationResult.failure(e.getMessage());
        }
    }

    private static String toJson(NotificationRequest r) {
        // v1: 단순 JSON (필요하면 v2에서 payload mapper/템플릿/채널별 포맷 확장)
        String title = escape(r.message().title());
        String body = escape(r.message().body());
        return "{"
                + "\"title\":" + (title == null ? "null" : "\"" + title + "\"") + ","
                + "\"body\":\"" + body + "\","
                + "\"recipientCount\":" + r.recipients().size()
                + "}";
    }

    private static String escape(String s) {
        if (s == null) return null;
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    private static String safe(String s) {
        if (s == null) return "";
        return s.length() > 500 ? s.substring(0, 500) + "..." : s;
    }
}
