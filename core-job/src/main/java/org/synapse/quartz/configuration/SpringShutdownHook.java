package org.synapse.quartz.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

/**
 * {@link SmartLifecycle} implementation to interrupt all currently executing Quartz jobs to allow them to complete
 * processing gracefully.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SpringShutdownHook implements SmartLifecycle {

    private final Scheduler scheduler;
    private boolean isRunning = false;

    public void stop(final Runnable runnable) {
        stop();
        runnable.run();
    }

    public void start() {
        log.info("SpringShutdownHook started.");
        isRunning = true;
    }

    public void stop() {
        log.info("Spring container is shutting down.");
        isRunning = false;

        try {
            interruptJobs();

            // Tell the scheduler to shutdown allowing jobs to complete
            scheduler.shutdown(true);
        } catch (SchedulerException e) {
            try {
                // Something has gone wrong so tell the scheduler to shutdown without allowing jobs to complete.
                scheduler.shutdown(false);
            } catch (SchedulerException ex) {
                log.error("Unable to shutdown the Quartz scheduler.", ex);
            }
        }
    }

    public boolean isAutoStartup() {
        return true;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public int getPhase() {
        return Integer.MAX_VALUE;
    }

    private void interruptJobs() throws SchedulerException {
        for (JobExecutionContext jobExecutionContext : scheduler.getCurrentlyExecutingJobs()) {

            final JobDetail jobDetail = jobExecutionContext.getJobDetail();
            log.info("Interrupting job key=[{}], group=[{}].", jobDetail.getKey().getName(),
                    jobDetail.getKey().getGroup());
            scheduler.interrupt(jobDetail.getKey());
        }
    }
}