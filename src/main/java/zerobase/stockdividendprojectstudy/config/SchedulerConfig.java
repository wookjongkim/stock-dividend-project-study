package zerobase.stockdividendprojectstudy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler threadPool = new ThreadPoolTaskScheduler();

        int n = Runtime.getRuntime().availableProcessors();
        // *2 or +1
        threadPool.setPoolSize(n);
        threadPool.initialize();

        taskRegistrar.setTaskScheduler(threadPool);
    }
}
