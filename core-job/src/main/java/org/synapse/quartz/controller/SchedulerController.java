package org.synapse.quartz.controller;

import lombok.RequiredArgsConstructor;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/scheduler")
@RequiredArgsConstructor
public class SchedulerController {
    private final Scheduler scheduler;

    @GetMapping(value = "/info")
    public SchedulerMetaData getSchedulerInformation() throws SchedulerException {
        return scheduler.getMetaData();
    }
}
