package org.synapse.quartz.job;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.quartz.*;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JobRequest {
    private String clusterName;
    private String jobName;
    private String clazz;
    private String jobDescription;
    private String triggerDescription;
    private TriggerType triggerType;
    private String expression;

    private Map<String, Object> dataMap = Map.of();

    @JsonIgnore
    public JobDetail getJobDetail() {
        return JobBuilder.newJob(getJobClass())
                .withIdentity(jobName, clusterName)
                .withDescription(jobDescription)
                .usingJobData(new JobDataMap(dataMap))
                .storeDurably()
                .build();
    }

    public JobDetail getJobDetail(JobDetail jobDetail) {
        Objects.requireNonNull(jobDetail);
        return jobDetail.getJobBuilder()
                .ofType(getJobClass())
                .withDescription(StringUtils.hasText(jobDescription) ? jobDescription : jobDetail.getDescription())
                .usingJobData(CollectionUtils.isEmpty(dataMap) ? jobDetail.getJobDataMap() : new JobDataMap(dataMap))
                .build();
    }

    @JsonIgnore
    public Trigger getTrigger() {
        Trigger trigger = null;
        switch (triggerType) {
            case SIMPLE: {
                trigger = TriggerBuilder.newTrigger()
                        .withIdentity(getTriggerKey())
                        .withDescription(triggerDescription)
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(Integer.parseInt(expression))
                                .repeatForever())
                        .forJob(getJobKey())
                        .build();
            }
            break;
            case CRON: {
                trigger = TriggerBuilder.newTrigger()
                        .withIdentity(getTriggerKey())
                        .withDescription(triggerDescription)
                        .withSchedule(CronScheduleBuilder.cronSchedule(expression))
                        .forJob(getJobKey())
                        .build();
            }
            break;
        }
        return trigger;
    }

    public Trigger getTrigger(Trigger trigger) {
        Objects.requireNonNull(trigger);
        switch (triggerType) {
            case SIMPLE: {
                return  TriggerBuilder.newTrigger()
                        .forJob(getJobKey())
                        .modifiedByCalendar(trigger.getCalendarName())
                        .usingJobData(CollectionUtils.isEmpty(dataMap) ? trigger.getJobDataMap() : new JobDataMap(dataMap))
                        .endAt(trigger.getEndTime())
                        .withPriority(trigger.getPriority())
                        .startAt(trigger.getStartTime())
                        .withIdentity(trigger.getKey())
                        .withDescription(StringUtils.hasText(triggerDescription) ? triggerDescription : trigger.getDescription())
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(Integer.parseInt(expression))
                                .repeatForever())
                        .build();
            }
            case CRON: {
                return  TriggerBuilder.newTrigger()
                        .forJob(getJobKey())
                        .modifiedByCalendar(trigger.getCalendarName())
                        .usingJobData(CollectionUtils.isEmpty(dataMap) ? trigger.getJobDataMap() : new JobDataMap(dataMap))
                        .endAt(trigger.getEndTime())
                        .withPriority(trigger.getPriority())
                        .startAt(trigger.getStartTime())
                        .withIdentity(trigger.getKey())
                        .withDescription(StringUtils.hasText(triggerDescription) ? triggerDescription : trigger.getDescription())
                        .withSchedule(CronScheduleBuilder.cronSchedule(expression))
                        .build();
            }
        }
        throw new IllegalStateException();
    }

    @JsonIgnore
    public JobKey getJobKey(){
        return JobKey.jobKey(jobName, clusterName);
    }

    @JsonIgnore
    public TriggerKey getTriggerKey(){
        return TriggerKey.triggerKey(jobName, clusterName);
    }

    public Class<? extends Job> getJobClass() {
        return JobUtils.getJobForName(clazz);
    }
}
