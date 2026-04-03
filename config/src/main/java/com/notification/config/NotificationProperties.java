package com.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "notification")
public class NotificationProperties {

    private boolean enabled = true;
    private Console console = new Console();
    private Webhook webhook = new Webhook();
    private Email email = new Email();
    private Slack slack = new Slack();

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Console getConsole() { return console; }
    public void setConsole(Console console) { this.console = console; }

    public Webhook getWebhook() { return webhook; }
    public void setWebhook(Webhook webhook) { this.webhook = webhook; }

    public Email getEmail() { return email; }
    public void setEmail(Email email) { this.email = email; }

    public Slack getSlack() { return slack; }
    public void setSlack(Slack slack) { this.slack = slack; }

    public static class Console {
        private boolean enabled = true;
        private String name = "console";

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class Webhook {
        private boolean enabled = false;
        private String name = "webhook";
        private String url;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
    }

    public static class Email {
        private boolean enabled = false;
        private String host = "127.0.0.1";
        private int port = 25;
        private String from = "noreply@example.com";
        private String heloHost = "localhost";

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }

        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }

        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }

        public String getHeloHost() { return heloHost; }
        public void setHeloHost(String heloHost) { this.heloHost = heloHost; }
    }

    public static class Slack {
        private boolean enabled = false;
        private String webhookUrl;
        private String displayName = "notification";

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public String getWebhookUrl() { return webhookUrl; }
        public void setWebhookUrl(String webhookUrl) { this.webhookUrl = webhookUrl; }

        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
    }
}
