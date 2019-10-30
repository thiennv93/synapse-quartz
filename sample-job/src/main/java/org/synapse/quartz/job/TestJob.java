package org.synapse.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TestJob extends QuartzJob {

	@Override
	protected void executeJob(JobExecutionContext context) {
		log.info("Long running job executing....");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			log.error("The thread sleep was interrupted.", e);
		}
		log.info("Long running job has been interrupted. Stopping.");
	}
}