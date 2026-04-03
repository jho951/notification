package com.notification.core.senders;

import com.notification.core.ChannelId;
import com.notification.core.NotificationRequest;
import com.notification.core.NotificationResult;
import com.notification.core.NotificationSender;
import com.notification.core.Recipient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

public final class SlackWebhookNotificationSender implements NotificationSender {
    private final ChannelId channelId = new ChannelId("slack");
    private final HttpClient httpClient;
    private final URI webhookUrl;
    private final Duration timeout;
    private final String displayName;

    public SlackWebhookNotificationSender(String webhookUrl) {
        this(URI.create(requireText(webhookUrl, "webhookUrl")), HttpClient.newHttpClient(), Duration.ofSeconds(5), "notification");
    }

    public SlackWebhookNotificationSender(URI webhookUrl, HttpClient httpClient, Duration timeout, String displayName) {
        this.webhookUrl = webhookUrl;
        this.httpClient = httpClient;
        this.timeout = timeout == null ? Duration.ofSeconds(5) : timeout;
        this.displayName = requireText(displayName, "displayName");
    }

    @Override
    public ChannelId channelId() {
        return channelId;
    }

    @Override
    public NotificationResult send(NotificationRequest request) {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(webhookUrl)
                    .timeout(timeout)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .POST(HttpRequest.BodyPublishers.ofString(toPayload(request), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return NotificationResult.success("slack-" + System.nanoTime());
            }
            return NotificationResult.failure("slack status=" + response.statusCode() + ", body=" + safe(response.body()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return NotificationResult.failure(e.getMessage());
        } catch (IOException e) {
            return NotificationResult.failure(e.getMessage());
        } catch (RuntimeException e) {
            return NotificationResult.failure(e.getMessage());
        }
    }

    private String toPayload(NotificationRequest request) {
        String text = displayName + "\n" + request.message().title() + "\n" + request.message().body();
        StringBuilder json = new StringBuilder();
        json.append('{');
        appendJsonField(json, "text", text);
        json.append(',');
        appendJsonField(json, "channel", firstRecipient(request));
        json.append(',');
        json.append("\"metadata\":{");
        boolean first = true;
        for (Map.Entry<String, String> entry : request.attributes().entrySet()) {
            if (!first) {
                json.append(',');
            }
            first = false;
            appendJsonString(json, entry.getKey());
            json.append(':');
            appendJsonString(json, entry.getValue());
        }
        json.append('}');
        json.append('}');
        return json.toString();
    }

    private String firstRecipient(NotificationRequest request) {
        Recipient recipient = request.recipients().get(0);
        return recipient.address();
    }

    private void appendJsonField(StringBuilder json, String key, String value) {
        appendJsonString(json, key);
        json.append(':');
        appendJsonString(json, value);
    }

    private void appendJsonString(StringBuilder json, String value) {
        json.append('"');
        for (char c : value.toCharArray()) {
            switch (c) {
                case '\\' -> json.append("\\\\");
                case '"' -> json.append("\\\"");
                case '\n' -> json.append("\\n");
                case '\r' -> json.append("\\r");
                case '\t' -> json.append("\\t");
                default -> json.append(c);
            }
        }
        json.append('"');
    }

    private String safe(String value) {
        if (value == null) {
            return "";
        }
        return value.length() > 500 ? value.substring(0, 500) + "..." : value;
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.trim();
    }
}
