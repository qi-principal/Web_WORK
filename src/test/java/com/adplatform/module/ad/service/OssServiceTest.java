package com.adplatform.module.ad.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OSS服务测试类
 *
 * @author andrew
 * @date 2023-12-19
 */
@SpringBootTest
public class OssServiceTest {

    @Autowired
    private OssService ossService;

    /**
     * 测试上传文件
     */
    @Test
    public void testUpload() throws Exception {
        // 创建模拟的MultipartFile
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );

        String url = ossService.upload(file, "test");

        assertNotNull(url);
        assertTrue(url.contains("test.jpg"));
    }

    /**
     * 测试删除文件
     */
    @Test
    public void testDelete() throws Exception {
        // 先上传文件
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );
        String url = ossService.upload(file, "test");

        // 删除文件
        assertDoesNotThrow(() -> ossService.delete(url));
    }

    /**
     * 测试上传非图片文件
     */
    @Test
    public void testUploadNonImage() throws Exception {
        // 创建模拟的文本文件
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            "test content".getBytes()
        );

        String url = ossService.upload(file, "test");

        assertNotNull(url);
        assertTrue(url.contains("test.txt"));
    }

    /**
     * 测试上传大文件
     */
    @Test
    public void testUploadLargeFile() throws Exception {
        // 创建1MB的测试数据
        byte[] content = new byte[1024 * 1024];
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "large.jpg",
            "image/jpeg",
            content
        );

        String url = ossService.upload(file, "test");

        assertNotNull(url);
        assertTrue(url.contains("large.jpg"));
    }

    /**
     * 测试删除不存在的文件
     */
    @Test
    public void testDeleteNonExistentFile() {
        String nonExistentUrl = "https://example.com/non-existent.jpg";
        
        assertDoesNotThrow(() -> ossService.delete(nonExistentUrl));
    }
} 