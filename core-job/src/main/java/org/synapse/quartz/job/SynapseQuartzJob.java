package org.synapse.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.InterruptableJob;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
public abstract class SynapseQuartzJob extends QuartzJobBean implements InterruptableJob {
    public void interrupt() {

    }
}
