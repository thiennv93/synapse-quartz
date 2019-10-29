package org.synapse.quartz.admin.dto;

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
    private String group = "default";
    @NotEmpty
    private String jobName;
    @NotNull
    private String jobClass;

    private Map<String, Object> dataMap;
}
