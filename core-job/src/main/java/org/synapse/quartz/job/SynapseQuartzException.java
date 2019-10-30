package org.synapse.quartz.job;

public class SynapseQuartzException extends RuntimeException {
    public SynapseQuartzException() {
    }

    public SynapseQuartzException(String message) {
        super(message);
    }

    public SynapseQuartzException(String message, Throwable cause) {
        super(message, cause);
    }
}
