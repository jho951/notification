package com.notification.config;

import com.notification.core.DefaultNotificationService;
import com.notification.core.NotificationSender;
import com.notification.core.NotificationService;
import com.notification.core.SenderRegistry;
import com.notification.core.senders.ConsoleNotificationSender;
import com.notification.core.senders.WebhookNotificationSender;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@AutoConfiguration
@EnableConfigurationProperties(NotificationProperties.class)
public class NotificationAutoConfiguration {

    @Bean
    public NotificationSender consoleNotificationSender() {
        return new ConsoleNotificationSender();
    }

    @Bean
    @ConditionalOnProperty(prefix = "notification.webhook", name = "enabled", havingValue = "true")
    public NotificationSender webhookNotificationSender(NotificationProperties props) {
        return new WebhookNotificationSender(props.getWebhook().getUrl());
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
