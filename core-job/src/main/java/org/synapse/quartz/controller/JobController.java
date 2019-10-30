package org.synapse.quartz.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.synapse.quartz.job.JobRequest;
import org.synapse.quartz.job.QuartzJobDetail;
import org.synapse.quartz.job.QuartzTrigger;
import org.synapse.quartz.job.TriggerType;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static org.quartz.JobKey.jobKey;

@RestController
@RequestMapping("/job")
@Slf4j
@RequiredArgsConstructor
public class JobController {
    private final Scheduler scheduler;

    @GetMapping("/{clusterName}/{jobName}")
    public QuartzJobDetail getJobDetail(@PathVariable("clusterName") String clusterName,
                                        @PathVariable("jobName") String jobName)
            throws SchedulerException {
        JobDetail jobDetail = scheduler.getJobDetail(jobKey(jobName, clusterName));
        Assert.notNull(jobDetail, String.format("Not found jobName{%s} in cluster{%s}", jobName, clusterName));
        QuartzJobDetail quartzJobDetail = new QuartzJobDetail();
        BeanUtils.copyProperties(jobDetail, quartzJobDetail);
        List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey(jobName, clusterName));
        if (!CollectionUtils.isEmpty(triggers)) {
            List<QuartzTrigger> quartzTriggers = new ArrayList<>();
            for (Trigger trigger : triggers) {
                QuartzTrigger quartzTrigger = new QuartzTrigger();
                BeanUtils.copyProperties(trigger, quartzTrigger);

                if (trigger instanceof SimpleTrigger) {
                    SimpleTrigger simpleTrigger = (SimpleTrigger) trigger;
                    quartzTrigger.setTriggerType(simpleTrigger.getClass().getSimpleName());
                    quartzTrigger.setRepeatInterval(simpleTrigger.getRepeatInterval());
                    quartzTrigger.setRepeatCount(simpleTrigger.getRepeatCount());
                    quartzTrigger.setTimesTriggered(simpleTrigger.getTimesTriggered());
                } else if (trigger instanceof CronTrigger) {
                    CronTrigger cronTrigger = (CronTrigger) trigger;
                    quartzTrigger.setTriggerType(cronTrigger.getClass().getSimpleName());
                    quartzTrigger.setTimeZone(cronTrigger.getTimeZone());
                    quartzTrigger.setCronExpression(cronTrigger.getCronExpression());
                    quartzTrigger.setExpressionSummary(cronTrigger.getExpressionSummary());
                }
                quartzTriggers.add(quartzTrigger);
            }
            quartzJobDetail.setTriggers(quartzTriggers);
        }
        return quartzJobDetail;
    }

    @PostMapping("/add")
    public void add(@RequestBody JobRequest jobRequest) throws SchedulerException {
        scheduler.addJob(jobRequest.getJobDetail(), false);
    }

    @PutMapping("/update")
    public void update(@Valid @RequestBody JobRequest jobRequest) throws SchedulerException {
        JobDetail jobDetail = scheduler.getJobDetail(jobRequest.getJobKey());
        scheduler.addJob(jobRequest.getJobDetail(jobDetail), true);
    }

    @PostMapping("/schedule")
    public void schedule(@RequestBody JobRequest jobRequest) throws SchedulerException {
        scheduler.scheduleJob(jobRequest.getTrigger());
    }

    @PostMapping("/{clusterName}/{jobName}/unschedule")
    public boolean unschedule(@PathVariable("clusterName") String clusterName, @PathVariable("jobName") String jobName)
            throws SchedulerException {
        return scheduler.unscheduleJob(TriggerKey.triggerKey(jobName, clusterName));
    }

    @PostMapping("/reschedule")
    public void reschedule(@RequestBody JobRequest jobRequest) throws SchedulerException {
        TriggerKey triggerKey = jobRequest.getTriggerKey();
        Trigger trigger = scheduler.getTrigger(triggerKey);
        if ((trigger instanceof SimpleTrigger && jobRequest.getTriggerType() != TriggerType.SIMPLE)
                || (trigger instanceof CronTrigger && jobRequest.getTriggerType() != TriggerType.CRON)) {
            scheduler.unscheduleJob(triggerKey);
            scheduler.scheduleJob(jobRequest.getTrigger(trigger));
        }else {
            scheduler.rescheduleJob(triggerKey, jobRequest.getTrigger(trigger));
        }
    }

    @DeleteMapping("/{clusterName}/{jobName}")
    public boolean delete(@PathVariable("clusterName") String clusterName, @PathVariable("jobName") String jobName)
            throws SchedulerException {
        scheduler.pauseTrigger(TriggerKey.triggerKey(jobName, clusterName));
        scheduler.unscheduleJob(TriggerKey.triggerKey(jobName, clusterName));
        return scheduler.deleteJob(JobKey.jobKey(jobName, clusterName));
    }

    @PostMapping("/{clusterName}/{jobName}/trigger")
    public void runNow(@PathVariable("clusterName") String clusterName, @PathVariable("jobName") String jobName)
            throws SchedulerException {
        scheduler.triggerJob(JobKey.jobKey(jobName, clusterName));
    }

    @PostMapping("/{clusterName}/{jobName}/pause")
    public void pause(@PathVariable("clusterName") String clusterName, @PathVariable("jobName") String jobName)
            throws SchedulerException {
        scheduler.pauseJob(JobKey.jobKey(jobName, clusterName));
        scheduler.pauseTrigger(TriggerKey.triggerKey(jobName, clusterName));
    }

    @PostMapping("/{clusterName}/{jobName}/resume")
    public void resume(@PathVariable("clusterName") String clusterName, @PathVariable("jobName") String jobName)
            throws SchedulerException {
        scheduler.resumeJob(JobKey.jobKey(jobName, clusterName));
        scheduler.resumeTrigger(TriggerKey.triggerKey(jobName, clusterName));
    }

    @PostMapping("/{clusterName}/{jobName}/stop")
    public void stop(@PathVariable("clusterName") String clusterName, @PathVariable("jobName") String jobName)
            throws UnableToInterruptJobException {
        scheduler.interrupt(JobKey.jobKey(jobName, clusterName));
    }
}
