package com.adplatform.module.delivery.listener;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.stereotype.Component;

/**
 * 广告投放任务监听器
 */
@Slf4j
@Component
public class DeliveryJobListener implements JobListener {

    @Override
    public String getName() {
        return "deliveryJobListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        String jobName = context.getJobDetail().getKey().getName();
        log.info("任务即将执行：{}", jobName);
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        String jobName = context.getJobDetail().getKey().getName();
        log.warn("任务被否决：{}", jobName);
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        String jobName = context.getJobDetail().getKey().getName();
        if (jobException != null) {
            log.error("任务执行失败：{}", jobName, jobException);
        } else {
            log.info("任务执行完成：{}", jobName);
        }
    }
} 