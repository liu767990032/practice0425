package com.logoxiang.practice.job;



import com.logoxiang.practice.taskDao.TimingConfigDao;
import com.logoxiang.practice.entity.TimingInterface;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.*;
import java.util.*;

/**
 * @Author: liuXiang
 * @Date: 2020/4/29 14:30
 */
@DisallowConcurrentExecution
public class TestTimeTaskJob implements Job {

    private Logger logger = LoggerFactory.getLogger(TestTimeTaskJob.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TimingConfigDao timingConfigDao;

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        TimingInterface inter = (TimingInterface) dataMap.get("inter");
        logger.info("PushLogToProvEntry[" + inter.getInterfaceName() + "]开始执行");
        // 执行状态及数据
        boolean isSuccessful = false;
        File file = null;
        try {
            System.out.println("***"+new Date());



        } catch (Exception e) {
            logger.error("PushLogToProvEntry[" + inter.getInterfaceName() + "]出错", e);
        }
        logger.info("PushLogToProvEntry[" + inter.getInterfaceName() + "]结束执行");
    }

}
