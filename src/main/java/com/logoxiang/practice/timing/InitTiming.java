package com.logoxiang.practice.timing;

import com.logoxiang.practice.service.TestTimeTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author: liuXiang
 * @Date: 2020/4/29 14:20
 */
@Component
public class InitTiming {

    @Autowired
private TestTimeTaskService testTimeTaskService;
    /**
     *
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void scanPushLogToProvConfig() {
        testTimeTaskService.addOrUpdateJob("1");
    }

}
