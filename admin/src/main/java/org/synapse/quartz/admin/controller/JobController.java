package org.synapse.quartz.admin.controller;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.synapse.quartz.admin.dto.JobDTO;

import java.util.List;

@RestController
@RequestMapping("/job")
public class JobController {
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/")
    public void search(@RequestParam(value = "clusterName", required = false) String clusterName,
                         @RequestParam(value = "triggerState", required = false) String triggerState,
                         @RequestParam(value = "jobName", required = false) String jobName, Pageable pageable) {

    }

    @PostMapping("/")
    public void addJob(@RequestBody JobDTO jobDTO){
        restTemplate.postForObject(serviceUrl(jobDTO.getGroup()) + "/job/", jobDTO, ResponseEntity.class);
    }
    @GetMapping(value = "/{group}/{name}/trigger")
    public void triggerJob(@PathVariable String group, @PathVariable String name) throws SchedulerException {
        String url = serviceUrl(group) + "/job/" + group + "/" + name + "/trigger";
        System.out.println(url);
        restTemplate.getForObject(url, ResponseEntity.class);
    }

    public String serviceUrl(String clusterName) {
        List<ServiceInstance> list = discoveryClient.getInstances(clusterName);
        if (list != null && list.size() > 0 ) {
            return list.get(0).getUri().toString();
        }
        return null;
    }
}
