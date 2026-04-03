package com.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "notification")
public class NotificationProperties {

    private Webhook webhook = new Webhook();

    public Webhook getWebhook() { return webhook; }
    public void setWebhook(Webhook webhook) { this.webhook = webhook; }

    public static class Webhook {
        private boolean enabled = false;
        private String url;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
    }
}
