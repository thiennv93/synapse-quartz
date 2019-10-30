package org.synapse.quartz.annotation;

import org.synapse.quartz.log.JobLogLevel;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SynapseJob {

    /**
     * cluster name
     */
    String cluster() default "";

    /**
     * job name
     */
    String name() default "";

    /**
     * User friendly description of job
     */
    String description() default "";

    /**
     * Default job log level
     */
    JobLogLevel logLevel() default JobLogLevel.INFO;


    /**
     * Create date info
     */
    String created() default "";
}
