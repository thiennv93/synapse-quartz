package org.synapse.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.synapse.quartz.TestService;
import org.synapse.quartz.annotation.SynapseJob;

@Slf4j
//@SynapseJob
public class TestJobSynapseQuartz extends SynapseQuartzJob {
	@Autowired
	private TestService testService;
	@Override
	public void execute(JobExecutionContext context) {
		log.info("Long running job executing....");
		try {
			testService.test();
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			log.error("The thread sleep was interrupted.", e);
		}
		log.info("Long running job has been interrupted. Stopping.");
	}

}