//package com.adplatform.module.delivery.service;
//
//import com.adplatform.module.delivery.dto.request.DeliveryTaskRequest;
//import com.adplatform.module.delivery.dto.response.DeliveryTaskResponse;
//import com.adplatform.module.delivery.entity.AdDeliveryTask;
//import com.adplatform.module.delivery.enums.DeliveryStatus;
//import com.adplatform.module.delivery.mapper.AdDeliveryTaskMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Date;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * 广告投放服务测试类
// */
//@SpringBootTest
//@Transactional
//public class AdDeliveryServiceTest {
//
//    @Autowired
//    private AdDeliveryService deliveryService;
//
//    @Autowired
//    private AdDeliveryTaskMapper taskMapper;
//
//    @Test
//    public void testCreateDeliveryTask() {
//        // 准备测试数据
//        DeliveryTaskRequest request = new DeliveryTaskRequest();
//        request.setAdId(1L);
//        request.setAdSpaceId(1L);
//        request.setScheduleTime(new Date(System.currentTimeMillis() + 3600000)); // 1小时后
//
//        // 执行测试
//        DeliveryTaskResponse response = deliveryService.createDeliveryTask(request);
//
//        // 验证结果
//        assertNotNull(response);
//        assertNotNull(response.getId());
//        assertEquals(request.getAdId(), response.getAdId());
//        assertEquals(request.getAdSpaceId(), response.getAdSpaceId());
//        assertEquals(DeliveryStatus.PENDING, response.getStatus());
//
//        // 验证数据库
//        AdDeliveryTask task = taskMapper.selectById(response.getId());
//        assertNotNull(task);
//        assertEquals(request.getAdId(), task.getAdId());
//        assertEquals(request.getAdSpaceId(), task.getAdSpaceId());
//        assertEquals(DeliveryStatus.PENDING.getCode(), task.getStatus());
//    }
//
//    @Test
//    public void testUpdateDeliveryTask() {
//        // 准备测试数据
//        DeliveryTaskRequest createRequest = new DeliveryTaskRequest();
//        createRequest.setAdId(1L);
//        createRequest.setAdSpaceId(1L);
//        createRequest.setScheduleTime(new Date(System.currentTimeMillis() + 3600000));
//        DeliveryTaskResponse createResponse = deliveryService.createDeliveryTask(createRequest);
//
//        // 准备更新数据
//        DeliveryTaskRequest updateRequest = new DeliveryTaskRequest();
//        updateRequest.setAdId(2L);
//        updateRequest.setAdSpaceId(2L);
//        updateRequest.setScheduleTime(new Date(System.currentTimeMillis() + 7200000)); // 2小时后
//
//        // 执行测试
//        DeliveryTaskResponse updateResponse = deliveryService.updateDeliveryTask(createResponse.getId(), updateRequest);
//
//        // 验证结果
//        assertNotNull(updateResponse);
//        assertEquals(updateRequest.getAdId(), updateResponse.getAdId());
//        assertEquals(updateRequest.getAdSpaceId(), updateResponse.getAdSpaceId());
//        assertEquals(DeliveryStatus.PENDING, updateResponse.getStatus());
//
//        // 验证数据库
//        AdDeliveryTask task = taskMapper.selectById(updateResponse.getId());
//        assertNotNull(task);
//        assertEquals(updateRequest.getAdId(), task.getAdId());
//        assertEquals(updateRequest.getAdSpaceId(), task.getAdSpaceId());
//        assertEquals(DeliveryStatus.PENDING.getCode(), task.getStatus());
//    }
//
//    @Test
//    public void testDeleteDeliveryTask() {
//        // 准备测试数据
//        DeliveryTaskRequest request = new DeliveryTaskRequest();
//        request.setAdId(1L);
//        request.setAdSpaceId(1L);
//        request.setScheduleTime(new Date(System.currentTimeMillis() + 3600000));
//        DeliveryTaskResponse response = deliveryService.createDeliveryTask(request);
//
//        // 执行测试
//        deliveryService.deleteDeliveryTask(response.getId());
//
//        // 验证数据库
//        AdDeliveryTask task = taskMapper.selectById(response.getId());
//        assertNull(task);
//    }
//
//    @Test
//    public void testGetDeliveryTask() {
//        // 准备测试数据
//        DeliveryTaskRequest request = new DeliveryTaskRequest();
//        request.setAdId(1L);
//        request.setAdSpaceId(1L);
//        request.setScheduleTime(new Date(System.currentTimeMillis() + 3600000));
//        DeliveryTaskResponse createResponse = deliveryService.createDeliveryTask(request);
//
//        // 执行测试
//        DeliveryTaskResponse getResponse = deliveryService.getDeliveryTask(createResponse.getId());
//
//        // 验证结果
//        assertNotNull(getResponse);
//        assertEquals(createResponse.getId(), getResponse.getId());
//        assertEquals(createResponse.getAdId(), getResponse.getAdId());
//        assertEquals(createResponse.getAdSpaceId(), getResponse.getAdSpaceId());
//        assertEquals(createResponse.getStatus(), getResponse.getStatus());
//    }
//}