//package com.adplatform.module.delivery.controller;
//
//import com.adplatform.module.delivery.dto.request.DeliveryTaskRequest;
//import com.adplatform.module.delivery.service.AdDeliveryService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Date;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
///**
// * 广告投放控制器测试类
// */
//@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
//public class AdDeliveryControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private AdDeliveryService deliveryService;
//
//    @Test
//    public void testCreateTask() throws Exception {
//        // 准备测试数据
//        DeliveryTaskRequest request = new DeliveryTaskRequest();
//        request.setAdId(1L);
//        request.setAdSpaceId(1L);
//        request.setScheduleTime(new Date(System.currentTimeMillis() + 3600000));
//
//        // 执行测试并验证结果
//        mockMvc.perform(post("/v1/delivery/tasks")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.adId").value(request.getAdId()))
//                .andExpect(jsonPath("$.adSpaceId").value(request.getAdSpaceId()))
//                .andExpect(jsonPath("$.status").value("PENDING"));
//    }
//
//    @Test
//    public void testUpdateTask() throws Exception {
//        // 准备测试数据
//        DeliveryTaskRequest createRequest = new DeliveryTaskRequest();
//        createRequest.setAdId(1L);
//        createRequest.setAdSpaceId(1L);
//        createRequest.setScheduleTime(new Date(System.currentTimeMillis() + 3600000));
//        Long taskId = deliveryService.createDeliveryTask(createRequest).getId();
//
//        DeliveryTaskRequest updateRequest = new DeliveryTaskRequest();
//        updateRequest.setAdId(2L);
//        updateRequest.setAdSpaceId(2L);
//        updateRequest.setScheduleTime(new Date(System.currentTimeMillis() + 7200000));
//
//        // 执行测试并验证结果
//        mockMvc.perform(put("/v1/delivery/tasks/" + taskId)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(updateRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(taskId))
//                .andExpect(jsonPath("$.adId").value(updateRequest.getAdId()))
//                .andExpect(jsonPath("$.adSpaceId").value(updateRequest.getAdSpaceId()))
//                .andExpect(jsonPath("$.status").value("PENDING"));
//    }
//
//    @Test
//    public void testDeleteTask() throws Exception {
//        // 准备测试数据
//        DeliveryTaskRequest request = new DeliveryTaskRequest();
//        request.setAdId(1L);
//        request.setAdSpaceId(1L);
//        request.setScheduleTime(new Date(System.currentTimeMillis() + 3600000));
//        Long taskId = deliveryService.createDeliveryTask(request).getId();
//
//        // 执行测试并验证结果
//        mockMvc.perform(delete("/v1/delivery/tasks/" + taskId))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void testGetTask() throws Exception {
//        // 准备测试数据
//        DeliveryTaskRequest request = new DeliveryTaskRequest();
//        request.setAdId(1L);
//        request.setAdSpaceId(1L);
//        request.setScheduleTime(new Date(System.currentTimeMillis() + 3600000));
//        Long taskId = deliveryService.createDeliveryTask(request).getId();
//
//        // 执行测试并验证结果
//        mockMvc.perform(get("/v1/delivery/tasks/" + taskId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(taskId))
//                .andExpect(jsonPath("$.adId").value(request.getAdId()))
//                .andExpect(jsonPath("$.adSpaceId").value(request.getAdSpaceId()))
//                .andExpect(jsonPath("$.status").value("PENDING"));
//    }
//
//    @Test
//    public void testPageTasks() throws Exception {
//        // 准备测试数据
//        DeliveryTaskRequest request = new DeliveryTaskRequest();
//        request.setAdId(1L);
//        request.setAdSpaceId(1L);
//        request.setScheduleTime(new Date(System.currentTimeMillis() + 3600000));
//        deliveryService.createDeliveryTask(request);
//
//        // 执行测试并验证结果
//        mockMvc.perform(get("/v1/delivery/tasks")
//                .param("current", "1")
//                .param("size", "10")
//                .param("adId", "1")
//                .param("adSpaceId", "1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.records").isArray())
//                .andExpect(jsonPath("$.total").isNumber())
//                .andExpect(jsonPath("$.size").value(10))
//                .andExpect(jsonPath("$.current").value(1));
//    }
//}