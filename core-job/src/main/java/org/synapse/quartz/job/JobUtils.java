package org.synapse.quartz.job;

import org.quartz.Job;
import org.springframework.util.Assert;

public final class JobUtils {

    public static Class<? extends Job> getJobForName(String className) {
        Class<?> jobClass;
        try {
            jobClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new SynapseQuartzException(e.getMessage());
        }
        Assert.isAssignable(Job.class, jobClass, "Class not instance of org.quartz.Job");
        return (Class<? extends Job>) jobClass;
    }

    private JobUtils() {
        throw new UnsupportedOperationException("Do not create instance");
    }
}
