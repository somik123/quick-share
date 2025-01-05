package org.somik.quick_share.config;

import org.somik.quick_share.service.MessageBoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {
    @Autowired
    MessageBoxService messageBoxService;

    // Runs every 5 mins
    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void deleteExpiredMessagesCronJob() {
        messageBoxService.deleteExpiredMessages();
    }
}
