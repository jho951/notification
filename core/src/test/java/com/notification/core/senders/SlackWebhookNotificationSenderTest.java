package com.notification.core.senders;

import com.notification.core.ChannelId;
import com.notification.core.NotificationMessage;
import com.notification.core.NotificationRequest;
import com.notification.core.NotificationResult;
import com.notification.core.Recipient;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SlackWebhookNotificationSenderTest {
    @Test
    void postsSlackPayload() throws Exception {
        AtomicReference<String> bodyRef = new AtomicReference<>();
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/hook", exchange -> handle(exchange, bodyRef));
        server.setExecutor(Executors.newSingleThreadExecutor());
        server.start();

        try {
            URI uri = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/hook");
            SlackWebhookNotificationSender sender = new SlackWebhookNotificationSender(
                    uri,
                    java.net.http.HttpClient.newHttpClient(),
                    Duration.ofSeconds(2),
                    "notification"
            );

            NotificationResult result = sender.send(new NotificationRequest(
                    new ChannelId("slack"),
                    new NotificationMessage("Deployment done", "Release completed"),
                    java.util.List.of(new Recipient("r1", "#alerts")),
                    Map.of("env", "test")
            ));

            assertTrue(result.success());
            String body = bodyRef.get();
            assertTrue(body.contains("\"text\""));
            assertTrue(body.contains("Deployment done"));
            assertTrue(body.contains("Release completed"));
            assertTrue(body.contains("\"channel\":\"#alerts\""));
            assertTrue(body.contains("\"env\":\"test\""));
        } finally {
            server.stop(0);
        }
    }

    private void handle(HttpExchange exchange, AtomicReference<String> bodyRef) throws IOException {
        try (InputStream input = exchange.getRequestBody()) {
            bodyRef.set(new String(input.readAllBytes(), StandardCharsets.UTF_8));
        }
        exchange.sendResponseHeaders(200, 0);
        exchange.getResponseBody().close();
    }
}
