package com.notification.core.senders;

import com.notification.core.ChannelId;
import com.notification.core.NotificationMessage;
import com.notification.core.NotificationRequest;
import com.notification.core.NotificationResult;
import com.notification.core.NotificationSender;
import com.notification.core.Recipient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class EmailNotificationSender implements NotificationSender {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneOffset.UTC);

    private final ChannelId channelId = new ChannelId("email");
    private final String host;
    private final int port;
    private final String from;
    private final String heloHost;
    private final Duration timeout;

    public EmailNotificationSender(String host, int port, String from) {
        this(host, port, from, "localhost", Duration.ofSeconds(5));
    }

    public EmailNotificationSender(String host, int port, String from, String heloHost, Duration timeout) {
        this.host = requireText(host, "host");
        this.port = port > 0 ? port : 25;
        this.from = requireText(from, "from");
        this.heloHost = requireText(heloHost, "heloHost");
        this.timeout = timeout == null ? Duration.ofSeconds(5) : timeout;
    }

    @Override
    public ChannelId channelId() {
        return channelId;
    }

    @Override
    public NotificationResult send(NotificationRequest request) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), socketTimeoutMillis());
            socket.setSoTimeout(socketTimeoutMillis());

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII))) {

                expect(readResponse(reader), 220, "SMTP greeting");
                sendCommand(writer, "EHLO " + heloHost);
                SmtpResponse ehlo = readResponse(reader);
                if (!ehlo.isSuccess()) {
                    sendCommand(writer, "HELO " + heloHost);
                    expect(readResponse(reader), 250, "SMTP HELO");
                }

                sendCommand(writer, "MAIL FROM:<" + from + ">");
                expect(readResponse(reader), 250, "SMTP MAIL FROM");

                List<Recipient> recipients = request.recipients();
                for (Recipient recipient : recipients) {
                    sendCommand(writer, "RCPT TO:<" + recipient.address() + ">");
                    expect(readResponse(reader), 250, "SMTP RCPT TO");
                }

                sendCommand(writer, "DATA");
                expect(readResponse(reader), 354, "SMTP DATA");

                writeData(writer, request);
                expect(readResponse(reader), 250, "SMTP message body");

                sendCommand(writer, "QUIT");
                readResponse(reader);

                return NotificationResult.success("email-" + System.nanoTime());
            }
        } catch (IOException e) {
            return NotificationResult.failure(e.getMessage());
        } catch (RuntimeException e) {
            return NotificationResult.failure(e.getMessage());
        }
    }

    private void writeData(BufferedWriter writer, NotificationRequest request) throws IOException {
        NotificationMessage message = request.message();
        writer.write("From: ");
        writer.write(from);
        writer.write("\r\n");
        writer.write("To: ");
        writer.write(joinRecipients(request.recipients()));
        writer.write("\r\n");
        writer.write("Subject: ");
        writer.write(message.title() == null ? "(no subject)" : message.title());
        writer.write("\r\n");
        writer.write("Date: ");
        writer.write(DATE_FORMATTER.format(Instant.now()));
        writer.write("\r\n");
        writer.write("Content-Type: text/plain; charset=UTF-8\r\n");
        writer.write("\r\n");
        writer.write(message.body());
        writer.write("\r\n");
        writer.write(".\r\n");
        writer.flush();
    }

    private String joinRecipients(List<Recipient> recipients) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < recipients.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(recipients.get(i).address());
        }
        return builder.toString();
    }

    private void sendCommand(BufferedWriter writer, String command) throws IOException {
        writer.write(command);
        writer.write("\r\n");
        writer.flush();
    }

    private SmtpResponse readResponse(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line == null || line.length() < 3) {
            throw new IOException("SMTP server closed connection");
        }

        int code = Integer.parseInt(line.substring(0, 3));
        StringBuilder text = new StringBuilder();
        text.append(line.length() > 4 ? line.substring(4) : "");

        while (line.length() > 3 && line.charAt(3) == '-') {
            line = reader.readLine();
            if (line == null || line.length() < 3) {
                throw new IOException("SMTP server closed connection");
            }
            text.append('\n').append(line.length() > 4 ? line.substring(4) : "");
        }

        return new SmtpResponse(code, text.toString());
    }

    private void expect(SmtpResponse response, int expectedCode, String action) {
        if (response.code() != expectedCode) {
            throw new IllegalStateException(action + " failed: " + response.code() + " " + response.text());
        }
    }

    private int socketTimeoutMillis() {
        long millis = timeout.toMillis();
        return millis > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) millis;
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.trim();
    }

    private record SmtpResponse(int code, String text) {
        boolean isSuccess() {
            return code >= 200 && code < 300;
        }
    }
}
