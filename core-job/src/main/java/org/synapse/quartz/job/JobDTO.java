package org.synapse.quartz.job;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.quartz.Job;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JobDTO {
    @NotEmpty
    private String clusterName = "default";
    @NotEmpty
    private String jobName;
    @NotNull
    private String jobClass;

    private Map<String, Object> dataMap = Map.of();
}
