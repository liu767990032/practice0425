package com.logoxiang.practice.service;

import com.logoxiang.practice.taskDao.TimingConfigDao;
import com.logoxiang.practice.entity.TimingInterface;
import com.logoxiang.practice.job.TestTimeTaskJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Author: liuXiang
 * @Date: 2020/4/29 14:23
 */
@Service
public class TestTimeTaskService {
    private Logger logger = LoggerFactory.getLogger(TestTimeTaskService.class);

    @Autowired
    private SchedulerFactoryBean schedulerFactory;

    @Autowired
    private TimingConfigDao timingConfigDao;

    public void addOrUpdateJob(String type) {
        try {
            Scheduler scheduler = schedulerFactory.getScheduler();
            // 获取定时接口配置列表常态还是异常
            List<TimingInterface> list = timingConfigDao.getTimingInterfaceList(type);
            if (!CollectionUtils.isEmpty(list)) {
                for (TimingInterface inter : list) {
                    // 判断当前接口是否运行
                    TriggerKey runningTriggerKey = new TriggerKey(inter.getInterfaceId());
                    CronTrigger runningTrigger = (CronTrigger) scheduler.getTrigger(runningTriggerKey);
                    if (runningTrigger != null) {
                        // 判断当前接口时候设为无效
                        if ("1".equals(inter.getState())) {
                            // 判断当前接口是否更改调度策略
                            if (!inter.getTriggerStrategy().equals(runningTrigger.getCronExpression())) {
                                delJob(scheduler, inter);
                                addJob(scheduler, inter);
                            }
                        } else {
                            delJob(scheduler, inter);
                        }
                    } else if ("1".equals(inter.getState())) {
                        addJob(scheduler, inter);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("加载监视器列表失败!", e);
        }
    }

    private void addJob(Scheduler scheduler, TimingInterface inter) {
        try {
            // 创建JobDetail
            JobDetail job = JobBuilder.newJob(TestTimeTaskJob.class).withIdentity(inter.getInterfaceId()).build();
            // 传参
            job.getJobDataMap().put("inter", inter);
            // 定义一个触发器
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(inter.getInterfaceId()).withSchedule(CronScheduleBuilder.cronSchedule(inter.getTriggerStrategy())).build();
            scheduler.scheduleJob(job, trigger);
            logger.info("成功加载监视器[" + inter.getInterfaceName() + "].");
        } catch (Exception e) {
            logger.error("加载监视器失败[" + inter.getInterfaceName() + "].", e);
        }
    }

    private void delJob(Scheduler scheduler, TimingInterface inter) {
        try {
            JobKey runningJobKey = new JobKey(inter.getInterfaceId());
            TriggerKey runningTriggerKey = new TriggerKey(inter.getInterfaceId());
            scheduler.pauseTrigger(runningTriggerKey);// 停止触发器
            scheduler.unscheduleJob(runningTriggerKey);// 移除触发器
            scheduler.deleteJob(runningJobKey);// 删除任务
            logger.info("成功删除监视器[" + inter.getInterfaceName() + "].");
        } catch (Exception e) {
            logger.error("删除监视器[" + inter.getInterfaceName() + "]失败.", e);
        }
    }

}
