package org.synapse.quartz.job;

import org.junit.Assert;
import org.junit.Test;

public class JobUtilsTest {

    @Test
    public void getJobForName() {
        Class clazz = JobUtils.getJobForName("org.synapse.quartz.job.QuartzJob");
        Assert.assertEquals(SynapseQuartzJob.class, clazz);
    }
}