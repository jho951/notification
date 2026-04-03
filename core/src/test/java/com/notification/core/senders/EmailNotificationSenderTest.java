package com.notification.core.senders;

import com.notification.core.ChannelId;
import com.notification.core.NotificationMessage;
import com.notification.core.NotificationRequest;
import com.notification.core.NotificationResult;
import com.notification.core.Recipient;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailNotificationSenderTest {
    @Test
    void sendsSmtpMessage() throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            int port = serverSocket.getLocalPort();
            AtomicReference<List<String>> commands = new AtomicReference<>(List.of());
            var executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> handleSmtp(serverSocket, commands));

            EmailNotificationSender sender = new EmailNotificationSender(
                    "127.0.0.1",
                    port,
                    "sender@example.com",
                    "localhost",
                    Duration.ofSeconds(2)
            );

            NotificationResult result = sender.send(new NotificationRequest(
                    new ChannelId("email"),
                    new NotificationMessage("Hello", "Body line 1\n.Body line 2"),
                    List.of(new Recipient("r1", "recipient@example.com")),
                    Map.of("source", "test")
            ));

            assertTrue(result.success());
            List<String> commandLines = commands.get();
            assertTrue(commandLines.stream().anyMatch(line -> line.startsWith("MAIL FROM:<sender@example.com>")));
            assertTrue(commandLines.stream().anyMatch(line -> line.startsWith("RCPT TO:<recipient@example.com>")));
            assertTrue(commandLines.stream().anyMatch(line -> line.contains("Subject: Hello")));
            assertTrue(commandLines.stream().anyMatch(line -> line.contains("Body line 1")));
            assertTrue(commandLines.stream().anyMatch(line -> line.contains(".Body line 2")));

            executor.shutdownNow();
        }
    }

    private void handleSmtp(ServerSocket serverSocket, AtomicReference<List<String>> commands) {
        List<String> lines = new ArrayList<>();
        try (Socket socket = serverSocket.accept();
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII))) {

            send(writer, "220 localhost ESMTP");
            expect(reader, lines, "EHLO localhost");
            send(writer, "250 localhost");
            expect(reader, lines, "MAIL FROM:<sender@example.com>");
            send(writer, "250 OK");
            expect(reader, lines, "RCPT TO:<recipient@example.com>");
            send(writer, "250 OK");
            expect(reader, lines, "DATA");
            send(writer, "354 End data");
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                lines.add(line);
                if (".".equals(line)) {
                    break;
                }
            }
            send(writer, "250 Queued");
            expect(reader, lines, "QUIT");
            send(writer, "221 Bye");
            commands.set(List.copyOf(lines));
        } catch (IOException ignored) {
            commands.set(List.copyOf(lines));
        }
    }

    private void expect(BufferedReader reader, List<String> lines, String expected) throws IOException {
        String line = reader.readLine();
        lines.add(line);
        assertEquals(expected, line);
    }

    private void send(BufferedWriter writer, String line) throws IOException {
        writer.write(line);
        writer.write("\r\n");
        writer.flush();
    }
}
