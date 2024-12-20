package com.adplatform.module.ad.controller;

import com.adplatform.module.ad.dto.MaterialDTO;
import com.adplatform.module.ad.service.MaterialService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 广告素材控制器测试类
 *
 * @author andrew
 * @date 2023-12-19
 */
@WebMvcTest(MaterialController.class)
public class MaterialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MaterialService materialService;

    @Test
    @WithMockUser(roles = "ADVERTISER")
    public void testUpload() throws Exception {
        // 准备测试数据
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "test content".getBytes()
        );

        // 配置Mock行为
        when(materialService.upload(any(), eq(1))).thenReturn(
            MaterialDTO.builder()
                .id(1L)
                .type(1)
                .url("http://test.com/test.jpg")
                .build()
        );

        // 执行测试
        mockMvc.perform(multipart("/api/v1/materials/upload")
                .file(file)
                .param("type", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADVERTISER")
    public void testAddMaterialToAd() throws Exception {
        mockMvc.perform(post("/api/v1/materials/1/ads/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADVERTISER")
    public void testRemoveMaterialFromAd() throws Exception {
        mockMvc.perform(delete("/api/v1/materials/1/ads/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADVERTISER")
    public void testListAdsByMaterialId() throws Exception {
        // 配置Mock行为
        when(materialService.listAdsByMaterialId(1L))
                .thenReturn(Arrays.asList(1L, 2L));

        // 执行测试
        mockMvc.perform(get("/api/v1/materials/1/ads"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADVERTISER")
    public void testGetById() throws Exception {
        // 配置Mock行为
        when(materialService.getById(1L)).thenReturn(
            MaterialDTO.builder()
                .id(1L)
                .type(1)
                .url("http://test.com/test.jpg")
                .build()
        );

        // 执行测试
        mockMvc.perform(get("/api/v1/materials/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADVERTISER")
    public void testListByAdId() throws Exception {
        mockMvc.perform(get("/api/v1/materials/ad/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADVERTISER")
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/materials/1"))
                .andExpect(status().isOk());
    }
} 