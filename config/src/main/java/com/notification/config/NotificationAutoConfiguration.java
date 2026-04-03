package com.notification.config;

import com.notification.core.DefaultNotificationService;
import com.notification.core.NotificationSender;
import com.notification.core.NotificationService;
import com.notification.core.SenderRegistry;
import com.notification.core.senders.ConsoleNotificationSender;
import com.notification.core.senders.EmailNotificationSender;
import com.notification.core.senders.SlackWebhookNotificationSender;
import com.notification.core.senders.WebhookNotificationSender;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@AutoConfiguration
@EnableConfigurationProperties(NotificationProperties.class)
public class NotificationAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "notification.console", name = "enabled", havingValue = "true", matchIfMissing = true)
    public NotificationSender consoleNotificationSender() {
        return new ConsoleNotificationSender();
    }

    @Bean
    @ConditionalOnProperty(prefix = "notification.webhook", name = "enabled", havingValue = "true")
    public NotificationSender webhookNotificationSender(NotificationProperties props) {
        return new WebhookNotificationSender(props.getWebhook().getUrl());
    }

    @Bean
    @ConditionalOnProperty(prefix = "notification.email", name = "enabled", havingValue = "true")
    public NotificationSender emailNotificationSender(NotificationProperties props) {
        NotificationProperties.Email email = props.getEmail();
        return new EmailNotificationSender(
                email.getHost(),
                email.getPort(),
                email.getFrom(),
                email.getHeloHost(),
                Duration.ofSeconds(5)
        );
    }

    @Bean
    @ConditionalOnProperty(prefix = "notification.slack", name = "enabled", havingValue = "true")
    public NotificationSender slackNotificationSender(NotificationProperties props) {
        NotificationProperties.Slack slack = props.getSlack();
        return new SlackWebhookNotificationSender(
                URI.create(slack.getWebhookUrl()),
                java.net.http.HttpClient.newHttpClient(),
                Duration.ofSeconds(5),
                slack.getDisplayName()
        );
    }

    @Bean
    public SenderRegistry senderRegistry(List<NotificationSender> senders) {
        return new SenderRegistry(new ArrayList<>(senders));
    }

    @Bean
    public NotificationService notificationService(SenderRegistry registry) {
        return new DefaultNotificationService(registry);
    }
}
