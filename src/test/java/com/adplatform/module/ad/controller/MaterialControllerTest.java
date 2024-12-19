package com.adplatform.module.ad.controller;

import com.adplatform.module.ad.enums.AdType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 素材控制器测试类
 *
 * @author andrew
 * @date 2023-12-19
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MaterialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 测试上传素材
     */
    @Test
    @WithMockUser(roles = "ADVERTISER")
    public void testUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/materials/upload")
                .file(file)
                .param("type", String.valueOf(AdType.IMAGE.getCode())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").exists());
    }

    /**
     * 测试获取素材详情
     */
    @Test
    @WithMockUser
    public void testGetById() throws Exception {
        // 先上传素材
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );

        String response = mockMvc.perform(multipart("/api/v1/materials/upload")
                .file(file)
                .param("type", String.valueOf(AdType.IMAGE.getCode())))
                .andReturn().getResponse().getContentAsString();

        // 从响应中提取ID
        String id = response.split("\"id\":")[1].split(",")[0];

        // 获取详情
        mockMvc.perform(get("/api/v1/materials/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    /**
     * 测试获取广告的素材列表
     */
    @Test
    @WithMockUser
    public void testListByAdId() throws Exception {
        mockMvc.perform(get("/api/v1/materials/ad/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    /**
     * 测试删除素材
     */
    @Test
    @WithMockUser(roles = "ADVERTISER")
    public void testDelete() throws Exception {
        // 先上传素材
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );

        String response = mockMvc.perform(multipart("/api/v1/materials/upload")
                .file(file)
                .param("type", String.valueOf(AdType.IMAGE.getCode())))
                .andReturn().getResponse().getContentAsString();

        // 从响应中提取ID
        String id = response.split("\"id\":")[1].split(",")[0];

        // 删除素材
        mockMvc.perform(delete("/api/v1/materials/" + id))
                .andExpect(status().isOk());
    }

    /**
     * 测试保存广告素材关联
     */
    @Test
    @WithMockUser(roles = "ADVERTISER")
    public void testSaveAdMaterials() throws Exception {
        mockMvc.perform(post("/api/v1/materials/ad/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[1, 2]"))
                .andExpect(status().isOk());
    }

    /**
     * 测试上传非法文件类型
     */
    @Test
    @WithMockUser(roles = "ADVERTISER")
    public void testUploadInvalidFileType() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.exe",
            "application/x-msdownload",
            "test content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/materials/upload")
                .file(file)
                .param("type", String.valueOf(AdType.IMAGE.getCode())))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试上传空文件
     */
    @Test
    @WithMockUser(roles = "ADVERTISER")
    public void testUploadEmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "empty.jpg",
            "image/jpeg",
            new byte[0]
        );

        mockMvc.perform(multipart("/api/v1/materials/upload")
                .file(file)
                .param("type", String.valueOf(AdType.IMAGE.getCode())))
                .andExpect(status().isBadRequest());
    }
} 