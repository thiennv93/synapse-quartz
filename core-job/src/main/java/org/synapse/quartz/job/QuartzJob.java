package org.synapse.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
public abstract class QuartzJob extends QuartzJobBean implements InterruptableJob {

    @Override
    protected void executeInternal(final JobExecutionContext context) throws JobExecutionException {
        executeJob(context);
    }

    protected abstract void executeJob(final JobExecutionContext context);

    public void interrupt() {
        log.info("Interrupted");
    }
}
