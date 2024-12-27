package org.somik.quick_share.config;

import org.somik.quick_share.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configurable
@EnableScheduling
public class SchedulerConfig {
    @Autowired
    MessageService messageService;

    @Scheduled(cron = "@hourly")
    public void hourlyCronJobs() {
        messageService.deleteExpiredMessages();
    }
}
