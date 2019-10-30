package org.synapse.quartz.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.synapse.quartz.annotation.SynapseJobAnnotationBeanPostProcessor;

@Configuration
public class QuartzSchedulerConfig {
    @Bean
    public SynapseJobAnnotationBeanPostProcessor synapseJobAnnotationBeanPostProcessor(){
        return new SynapseJobAnnotationBeanPostProcessor("org.synapse");
    }
}