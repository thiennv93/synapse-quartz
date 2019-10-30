package org.synapse.quartz;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TestService {
    public void test(){
        log.info("Im test service");
    }
}
