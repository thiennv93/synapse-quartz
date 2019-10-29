package org.synapse.quartz.controller;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.synapse.quartz.job.JobDTO;

import java.util.List;

@RestController
@RequestMapping("/job")
public class JobController {
    @Autowired
    private Scheduler scheduler;

    @PostMapping
    public void create(@RequestBody JobDTO jobDTO) throws SchedulerException, ClassNotFoundException {
        JobDetail jobDetail = JobBuilder.newJob((Class<? extends Job>) Class.forName(jobDTO.getJobClass()))
                .withIdentity(jobDTO.getJobName(), jobDTO.getClusterName())
                .usingJobData(new JobDataMap(jobDTO.getDataMap()))
                .storeDurably()
                .build();
        scheduler.addJob(jobDetail, true);
    }

//    @GetMapping("/job/{jobId}")
//    public JobWrapper getJobById(@PathVariable("jobId") String jobId){
//        return null;
//    }
//
//    @GetMapping
//    public List<JobWrapper> search(Pageable pageable){
//        return null;
//    }

    @PostMapping(value = "/trigger")
    public void trigger(@RequestBody JobDTO jobDto) throws SchedulerException {
        JobKey jobKey = new JobKey(jobDto.getJobName(), jobDto.getClusterName());
        JobDetail job = scheduler.getJobDetail(jobKey);
        Assert.notNull(job, "Job not found");
        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobKey)
                .withIdentity(jobKey.getName(), jobKey.getGroup())
                .usingJobData(new JobDataMap(jobDto.getDataMap()))
                .startNow()
                .build();
        scheduler.scheduleJob(trigger);
    }

    @PutMapping
    public void update(@RequestBody JobDTO jobDTO) throws SchedulerException, ClassNotFoundException {
        JobDetail job = scheduler.getJobDetail(new JobKey(jobDTO.getJobName(), jobDTO.getClusterName()));
        Assert.notNull(job, "Job not found");
        job = JobBuilder.newJob((Class<? extends Job>) Class.forName(jobDTO.getJobClass()))
                .withIdentity(jobDTO.getJobName(), jobDTO.getClusterName())
                .usingJobData(new JobDataMap(jobDTO.getDataMap()))
                .storeDurably()
                .build();
        scheduler.addJob(job, true);
    }

    @DeleteMapping("/{clusterName}/{jobName}")
    public boolean delete(@PathVariable("clusterName") String clusterName, @PathVariable("jobName") String jobName)
            throws SchedulerException {
        JobDetail job = scheduler.getJobDetail(JobKey.jobKey(jobName, clusterName));
        Assert.notNull(job, "Job not found");
        return scheduler.deleteJob(job.getKey());
    }

    @PostMapping("/{clusterName}/{jobName}/unschedule")
    public boolean unschedule(@PathVariable("clusterName") String clusterName, @PathVariable("jobName") String jobName)
            throws SchedulerException {
        return scheduler.unscheduleJob(TriggerKey.triggerKey(jobName, clusterName));
    }

    @GetMapping("/{clusterName}/{jobName}/state")
    public String getJobState(@PathVariable("clusterName") String clusterName, @PathVariable("jobName") String jobName)
            throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, clusterName);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobDetail.getKey());
        if(triggers != null && triggers.size() > 0){
            for (Trigger trigger : triggers) {
                return scheduler.getTriggerState(trigger.getKey()).name();
            }
        }
        return null;
    }

    @PostMapping("/{clusterName}/{jobName}/start")
    public void start(@PathVariable("clusterName") String clusterName, @PathVariable("jobName") String jobName)
            throws SchedulerException {
        scheduler.triggerJob(JobKey.jobKey(jobName, clusterName));
    }

    @PostMapping("/{clusterName}/{jobName}/pause")
    public void pause(@PathVariable("clusterName") String clusterName, @PathVariable("jobName") String jobName)
            throws SchedulerException {
        scheduler.pauseJob(JobKey.jobKey(jobName, clusterName));
    }

    @PostMapping("/{clusterName}/{jobName}/resume")
    public void resume(@PathVariable("clusterName") String clusterName, @PathVariable("jobName") String jobName)
            throws SchedulerException {
        scheduler.resumeJob(JobKey.jobKey(jobName, clusterName));
    }

    @PostMapping("/{clusterName}/{jobName}/stop")
    public void stop(@PathVariable("clusterName") String clusterName, @PathVariable("jobName") String jobName)
            throws UnableToInterruptJobException {
        scheduler.interrupt(JobKey.jobKey(jobName, clusterName));
    }
}
