package com.adplatform.module.delivery.service.impl;

import com.adplatform.module.ad.entity.Advertisement;
import com.adplatform.module.ad.mapper.AdvertisementMapper;
import com.adplatform.module.delivery.converter.DeliveryConverter;
import com.adplatform.module.delivery.dto.request.DeliveryTaskRequest;
import com.adplatform.module.delivery.dto.response.DeliveryTaskResponse;
import com.adplatform.module.delivery.dto.response.News;
import com.adplatform.module.delivery.entity.AdDeliveryJob;
import com.adplatform.module.delivery.entity.AdDeliveryTask;
import com.adplatform.module.delivery.entity.Time;
import com.adplatform.module.delivery.entity.Track;
import com.adplatform.module.delivery.enums.DeliveryStatus;
//import com.adplatform.module.delivery.executor.SimpleDeliveryExecutor;
import com.adplatform.module.delivery.mapper.AdDeliveryJobMapper;
import com.adplatform.module.delivery.mapper.AdDeliveryTaskMapper;
import com.adplatform.module.delivery.mapper.TrackMapper;
import com.adplatform.module.delivery.service.AdDeliveryService;
import com.adplatform.module.website.mapper.WebsiteMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 广告投放服务实现类
 */
@Service
public class AdDeliveryServiceImpl implements AdDeliveryService {

    @Autowired
    private AdDeliveryTaskMapper taskMapper;

    @Autowired
    private AdDeliveryJobMapper jobMapper;
    @Autowired
    private TrackMapper trackMapper;
    @Resource
    private AdvertisementMapper  advertisementMapper;
    @Resource
    private WebsiteMapper websiteMapper;
    @Resource
    private AdDeliveryTaskMapper adDeliveryTaskMapper;

//    @Autowired
//    private SimpleDeliveryExecutor deliveryExecutor;

    @Override
    public void createDeliveryTask(DeliveryTaskRequest request) {

        taskMapper.insetTask(request);


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeliveryTaskResponse updateDeliveryTask(Long taskId, DeliveryTaskRequest request) {
        AdDeliveryTask task = taskMapper.selectById(taskId);
        if (Objects.isNull(task)) {
            throw new RuntimeException("投放任务不存在");
        }

        // 更新任务信息
        task.setAdId(request.getAdId());
        task.setAdSpaceId(request.getAdSpaceId());
        task.setStartTime(request.getStartTime());
        task.setEndTime(request.getEndTime());
        task.setPriority(request.getPriority());

        taskMapper.updateById(task);

        return DeliveryConverter.INSTANCE.toResponse(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDeliveryTask(Long taskId) {
        AdDeliveryTask task = taskMapper.selectById(taskId);
        if (Objects.isNull(task)) {
            throw new RuntimeException("投放任务不存在");
        }

        // 删除任务
        taskMapper.deleteById(taskId);

        // 删除相关的作业
        jobMapper.delete(new LambdaQueryWrapper<AdDeliveryJob>()
                .eq(AdDeliveryJob::getTaskId, taskId));
    }

    @Override
    public DeliveryTaskResponse getDeliveryTask(Long taskId) {
        AdDeliveryTask task = taskMapper.selectById(taskId);
        if (Objects.isNull(task)) {
            throw new RuntimeException("投放任务不存在");
        }
        return DeliveryConverter.INSTANCE.toResponse(task);
    }

    @Override
    public IPage<News> pageDeliveryTasks(Page<News> page, Long adId, Long adSpaceId, Integer status) {
        Page<AdDeliveryTask> taskPage = taskMapper.selectPage(
            new Page<>(page.getCurrent(), page.getSize()),
            new LambdaQueryWrapper<AdDeliveryTask>()
                .eq(Objects.nonNull(adId), AdDeliveryTask::getAdId, adId)
                .eq(Objects.nonNull(adSpaceId), AdDeliveryTask::getAdSpaceId, adSpaceId)
                .orderByDesc(AdDeliveryTask::getCreateTime)
        );

        // 转换为响应DTO
        IPage<News> responsePage = new Page<>(
            taskPage.getCurrent(),
            taskPage.getSize(),
            taskPage.getTotal()
        );

        List<News> responseList = new ArrayList<>();
        for (AdDeliveryTask task : taskPage.getRecords()) {
//            DeliveryTaskResponse response = DeliveryConverter.INSTANCE.toResponse(task);

            News response=new News();
            // 假设您有一个方法可以根据adId查询广告标题
            String adTitle = advertisementMapper.getAdTitleById(task.getAdId());
            response.setAdTitle(adTitle); // 将广告标题设置到响应对象中

            // 假设您有一个方法可以根据adId查询广告标题
            String  spaceName=websiteMapper.getAdTitleById(task.getAdSpaceId());
            response.setSpaceName(spaceName); // 将广告标题设置到响应对象中

            Time time=adDeliveryTaskMapper.find(task.getId());
            response.setStartTime(time.getStartTime());
            response.setEndTime(time.getEndTime());
            responseList.add(response);
            response.setStatus(task.getStatus());
            response.setId(task.getId());
            System.out.println("hhhhhhhhhhhhhhhhhhhhhhh ");
            System.out.println(task.getStatus());
        }
        responsePage.setRecords(responseList);
        return responsePage;

    }

    @Override
    public void updateStatus(Long taskId, Integer status) {
       adDeliveryTaskMapper.updateStatus(taskId,status);
    }

    @Override
    public List<Advertisement> getAd() {
        List<Integer> index=adDeliveryTaskMapper.selectIDS();
        List<Advertisement> list=advertisementMapper.getAll(index);
        return list;
    }

    @Override
    public Advertisement recommendAd(String cookieValue){
        List<Advertisement> list = getAd();
        if (cookieValue == null){
            int index = ThreadLocalRandom.current().nextInt(list.size());
            return list.get(index);
        }
        if (list.isEmpty()) {
           return  list.get(ThreadLocalRandom.current().nextInt(list.size()));
        }
        else {
            List<Track> tracks = trackMapper.findByCookieValue(cookieValue);
            Map<String, Integer> frequencyMap = new HashMap<>();
            for (Track track : tracks) {
                frequencyMap.put(track.getGoodsUrl(), frequencyMap.getOrDefault(track.getGoodsUrl(), 0) + 1);
            }
            List<String> urlList = new ArrayList<>(frequencyMap.keySet());
            urlList.sort((a, b) -> frequencyMap.get(b) - frequencyMap.get(a));
            for(String url : urlList){
                for (Advertisement ad : list){
                    if(ad.getClickUrl().equals(url)){
                        return ad;
                    }
                }
            }
            return list.get(ThreadLocalRandom.current().nextInt(list.size()));
        }

    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void executeDeliveryTask(Long taskId) {
//        AdDeliveryTask task = taskMapper.selectById(taskId);
//        if (Objects.isNull(task)) {
//            throw new RuntimeException("投放任务不存在");
//        }
//
//        // 创建执行作业
//        AdDeliveryJob job = new AdDeliveryJob();
//        job.setTaskId(taskId);
//        job.setDeliveryStatus(DeliveryStatus.RUNNING);
//        jobMapper.insert(job);
//
//        try {
//            // 更新任务状态为执行中
//            task.setDeliveryStatus(DeliveryStatus.RUNNING);
//            taskMapper.updateById(task);
//
//            // 执行广告投放
//            String result = deliveryExecutor.execute(task);
//
//            // 更新作业状态为已完成
//            job.setDeliveryStatus(DeliveryStatus.COMPLETED);
//            job.setResult(result);
//            jobMapper.updateById(job);
//
//            // 更新任务状态为已完成
//            task.setDeliveryStatus(DeliveryStatus.COMPLETED);
//            taskMapper.updateById(task);
//
//        } catch (Exception e) {
//            // 更新作业状态为暂停
//            job.setDeliveryStatus(DeliveryStatus.PAUSED);
//            job.setResult("投放失败：" + e.getMessage());
//            jobMapper.updateById(job);
//
//            // 更新任务状态为暂停
//            task.setDeliveryStatus(DeliveryStatus.PAUSED);
//            taskMapper.updateById(task);
//
//            throw new RuntimeException("执行投放任务失败", e);
//        }
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void pauseDeliveryTask(Long taskId) {
//        AdDeliveryTask task = taskMapper.selectById(taskId);
//        if (Objects.isNull(task)) {
//            throw new RuntimeException("投放任务不存在");
//        }
//
//        // 只有运行中的任务可以暂停
//        if (task.getDeliveryStatus() != DeliveryStatus.RUNNING) {
//            throw new RuntimeException("只有运行中的任务可以暂停");
//        }
//
//        // 更新任务状态为暂停
//        task.setDeliveryStatus(DeliveryStatus.PAUSED);
//        taskMapper.updateById(task);
//
//        // 暂停相关的运行中作业
//        jobMapper.update(null, new LambdaQueryWrapper<AdDeliveryJob>()
//            .eq(AdDeliveryJob::getTaskId, taskId)
//            .eq(AdDeliveryJob::getStatus, DeliveryStatus.RUNNING.getCode())
//            .set(AdDeliveryJob::getStatus, DeliveryStatus.PAUSED.getCode()));
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void resumeDeliveryTask(Long taskId) {
//        AdDeliveryTask task = taskMapper.selectById(taskId);
//        if (Objects.isNull(task)) {
//            throw new RuntimeException("投放任务不存在");
//        }
//
//        // 只有暂停的任务可以恢复
//        if (task.getDeliveryStatus() != DeliveryStatus.PAUSED) {
//            throw new RuntimeException("只有暂停的任务可以恢复");
//        }
//
//        // 更新任务状态为运行中
//        task.setDeliveryStatus(DeliveryStatus.RUNNING);
//        taskMapper.updateById(task);
//
//        // 重新执行任务
//        executeDeliveryTask(taskId);
//    }
//
//    @Override
//    public String getTaskExecutionStatus(Long taskId) {
//        AdDeliveryTask task = taskMapper.selectById(taskId);
//        if (Objects.isNull(task)) {
//            throw new RuntimeException("投放任务不存在");
//        }
//
//        // 获取最新的作业状态
//        AdDeliveryJob latestJob = jobMapper.selectOne(
//            new LambdaQueryWrapper<AdDeliveryJob>()
//                .eq(AdDeliveryJob::getTaskId, taskId)
//                .orderByDesc(AdDeliveryJob::getCreateTime)
//                .last("LIMIT 1")
//        );
//
//        if (Objects.isNull(latestJob)) {
//            return "未开始执行";
//        }
//
//        return String.format("任务状态: %s, 作业状态: %s, 执行结果: %s",
//            task.getDeliveryStatus().getDescription(),
//            latestJob.getDeliveryStatus().getDescription(),
//            latestJob.getResult());
//    }
}