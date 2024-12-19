package com.adplatform.module.ad.service;

import com.adplatform.module.ad.dto.MaterialDTO;
import com.adplatform.module.ad.enums.AdType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 素材服务测试类
 *
 * @author andrew
 * @date 2023-12-19
 */
@SpringBootTest
@Transactional
public class MaterialServiceTest {

    @Autowired
    private MaterialService materialService;

    /**
     * 测试上传素材
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

        MaterialDTO result = materialService.upload(file, AdType.IMAGE.getCode());

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(AdType.IMAGE.getCode(), result.getType());
        assertNotNull(result.getUrl());
    }

    /**
     * 测试获取素材详情
     */
    @Test
    public void testGetById() throws Exception {
        // 先上传素材
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );
        MaterialDTO uploaded = materialService.upload(file, AdType.IMAGE.getCode());

        // 获取详情
        MaterialDTO result = materialService.getById(uploaded.getId());

        assertNotNull(result);
        assertEquals(uploaded.getId(), result.getId());
        assertEquals(uploaded.getUrl(), result.getUrl());
    }

    /**
     * 测试获取广告的素材列表
     */
    @Test
    public void testListByAdId() throws Exception {
        // 先上传素材
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );
        MaterialDTO material = materialService.upload(file, AdType.IMAGE.getCode());

        // 关联到广告
        Long adId = 1L;
        materialService.saveAdMaterials(adId, Arrays.asList(material.getId()));

        // 获取列表
        List<MaterialDTO> results = materialService.listByAdId(adId);

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(material.getId(), results.get(0).getId());
    }

    /**
     * 测试删除素材
     */
    @Test
    public void testDelete() throws Exception {
        // 先上传素材
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );
        MaterialDTO material = materialService.upload(file, AdType.IMAGE.getCode());

        // 删除素材
        materialService.delete(material.getId());

        // 验证是否删除成功
        assertThrows(RuntimeException.class, () -> {
            materialService.getById(material.getId());
        });
    }

    /**
     * 测试保存广告素材关联
     */
    @Test
    public void testSaveAdMaterials() throws Exception {
        // 先上传两个素材
        MockMultipartFile file1 = new MockMultipartFile(
            "file1",
            "test1.jpg",
            "image/jpeg",
            "test image content 1".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile(
            "file2",
            "test2.jpg",
            "image/jpeg",
            "test image content 2".getBytes()
        );
        MaterialDTO material1 = materialService.upload(file1, AdType.IMAGE.getCode());
        MaterialDTO material2 = materialService.upload(file2, AdType.IMAGE.getCode());

        // 关联到广告
        Long adId = 1L;
        List<Long> materialIds = Arrays.asList(material1.getId(), material2.getId());
        materialService.saveAdMaterials(adId, materialIds);

        // 验证关联是否成功
        List<MaterialDTO> results = materialService.listByAdId(adId);
        assertNotNull(results);
        assertEquals(2, results.size());
    }
} 