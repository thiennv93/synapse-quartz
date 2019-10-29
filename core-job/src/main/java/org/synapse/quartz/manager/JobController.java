package org.synapse.quartz.manager;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.synapse.quartz.job.JobDTO;

@RestController
@RequestMapping("/job")
public class JobController {
    @Autowired
    protected Scheduler scheduler;

    @PostMapping("/")
    public void create(@RequestBody JobDTO jobDTO) throws SchedulerException, ClassNotFoundException {
        JobDetail jobDetail = JobBuilder.newJob((Class<? extends Job>) Class.forName(jobDTO.getJobClass()))
                .withIdentity(jobDTO.getJobName(), jobDTO.getGroup())
//                .usingJobData(new JobDataMap(dataMap))
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

    @GetMapping(value = "/{group}/{name}/trigger")
    public void triggerJob(@PathVariable String group, @PathVariable String name) throws SchedulerException {
        JobKey jobKey = new JobKey(name, group);
        JobDetail job = scheduler.getJobDetail(jobKey);
        Assert.notNull(job, "Job not found");
        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobKey)
                .withIdentity(name, group)
//                .usingJobData(new JobDataMap(dataMap))
                .startNow()
                .build();
        scheduler.scheduleJob(trigger);
    }

//    @PutMapping(value = "/{group}/{name}")
//    public String updateJob(@PathVariable String group, @PathVariable String name) throws SchedulerException {
//        JobDetail job = scheduler.getJobDetail(new JobKey(name, group));
//        Assert.notNull(job, "Job not found");
//        scheduler.addJob(form.getJobDetails(job), true);
//    }
    @DeleteMapping("/{group}/{name}")
    public void deleteJob(@PathVariable String group, @PathVariable String name) throws SchedulerException {
        JobDetail job = scheduler.getJobDetail(new JobKey(name, group));
        Assert.notNull(job, "Job not found");
        scheduler.deleteJob(job.getKey());
    }
}
