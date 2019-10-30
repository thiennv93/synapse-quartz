package org.synapse.quartz.job;

import org.junit.Assert;
import org.junit.Test;
import org.quartz.JobExecutionContext;

import static org.junit.Assert.*;

public class JobUtilsTest {

    @Test
    public void getJobForName() {
        Class clazz = JobUtils.getJobForName("org.synapse.quartz.job.QuartzJob");
        Assert.assertEquals(QuartzJob.class, clazz);
    }
}