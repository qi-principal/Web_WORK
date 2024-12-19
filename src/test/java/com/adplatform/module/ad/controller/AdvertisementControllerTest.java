package com.adplatform.module.ad.controller;

import com.adplatform.module.ad.dto.AdvertisementDTO;
import com.adplatform.module.ad.enums.AdType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 广告控制器测试类
 *
 * @author andrew
 * @date 2023-12-19
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdvertisementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 测试创建广告
     */
    @Test
    @WithMockUser(roles = "ADVERTISER")
    public void testCreate() throws Exception {
        AdvertisementDTO dto = createAdvertisementDTO();

        mockMvc.perform(post("/api/v1/ads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(dto.getTitle()));
    }

    /**
     * 测试更新广告
     */
    @Test
    @WithMockUser(roles = "ADVERTISER")
    public void testUpdate() throws Exception {
        // 先创建广告
        AdvertisementDTO dto = createAdvertisementDTO();
        String response = mockMvc.perform(post("/api/v1/ads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();
        AdvertisementDTO created = objectMapper.readValue(response, AdvertisementDTO.class);

        // 更新广告
        created.setTitle("更新后的标题");
        mockMvc.perform(put("/api/v1/ads/" + created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("更新后的标题"));
    }

    /**
     * 测试获取广告详情
     */
    @Test
    @WithMockUser
    public void testGetById() throws Exception {
        // 先创建广告
        AdvertisementDTO dto = createAdvertisementDTO();
        String response = mockMvc.perform(post("/api/v1/ads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();
        AdvertisementDTO created = objectMapper.readValue(response, AdvertisementDTO.class);

        // 获取详情
        mockMvc.perform(get("/api/v1/ads/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId()));
    }

    /**
     * 测试分页查询
     */
    @Test
    @WithMockUser
    public void testPage() throws Exception {
        mockMvc.perform(get("/api/v1/ads")
                .param("current", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records").exists());
    }

    /**
     * 测试提交审核
     */
    @Test
    @WithMockUser(roles = "ADVERTISER")
    public void testSubmit() throws Exception {
        // 先创建广告
        AdvertisementDTO dto = createAdvertisementDTO();
        String response = mockMvc.perform(post("/api/v1/ads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();
        AdvertisementDTO created = objectMapper.readValue(response, AdvertisementDTO.class);

        // 提交审核
        mockMvc.perform(post("/api/v1/ads/" + created.getId() + "/submit"))
                .andExpect(status().isOk());
    }

    /**
     * 测试审核通过
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testApprove() throws Exception {
        // 先创建并提交广告
        AdvertisementDTO dto = createAdvertisementDTO();
        String response = mockMvc.perform(post("/api/v1/ads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();
        AdvertisementDTO created = objectMapper.readValue(response, AdvertisementDTO.class);

        mockMvc.perform(post("/api/v1/ads/" + created.getId() + "/submit"))
                .andExpect(status().isOk());

        // 审核通过
        mockMvc.perform(post("/api/v1/ads/" + created.getId() + "/approve"))
                .andExpect(status().isOk());
    }

    /**
     * 创建测试用的广告DTO
     */
    private AdvertisementDTO createAdvertisementDTO() {
        AdvertisementDTO dto = new AdvertisementDTO();
        dto.setTitle("测试广告");
        dto.setDescription("这是一个测试广告");
        dto.setType(AdType.IMAGE.getCode());
        dto.setBudget(new BigDecimal("1000"));
        dto.setDailyBudget(new BigDecimal("100"));
        dto.setStartTime(LocalDateTime.now().plusDays(1));
        dto.setEndTime(LocalDateTime.now().plusDays(10));
        return dto;
    }
} 