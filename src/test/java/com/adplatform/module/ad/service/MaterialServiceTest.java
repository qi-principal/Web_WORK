package com.adplatform.module.ad.service;

import com.adplatform.module.ad.converter.AdConverter;
import com.adplatform.module.ad.dto.MaterialDTO;
import com.adplatform.module.ad.entity.AdMaterialRelation;
import com.adplatform.module.ad.entity.Material;
import com.adplatform.module.ad.mapper.AdMaterialRelationMapper;
import com.adplatform.module.ad.mapper.MaterialMapper;
import com.adplatform.module.ad.service.impl.MaterialServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 广告素材服务测试类
 *
 * @author andrew
 * @date 2023-12-19
 */
@ExtendWith(MockitoExtension.class)
public class MaterialServiceTest {

    @Mock
    private MaterialMapper materialMapper;

    @Mock
    private AdMaterialRelationMapper relationMapper;

    @Mock
    private AdConverter adConverter;

    @Mock
    private OssService ossService;

    @InjectMocks
    private MaterialServiceImpl materialService;

    private Material testMaterial;
    private MaterialDTO testMaterialDTO;
    private AdMaterialRelation testRelation;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        testMaterial = new Material();
        testMaterial.setId(1L);
        testMaterial.setType(1);
        testMaterial.setContent("测试内容");
        testMaterial.setUrl("http://test.com/image.jpg");
        testMaterial.setSize(1024L);
        testMaterial.setCreateTime(LocalDateTime.now());

        testMaterialDTO = MaterialDTO.builder()
                .id(1L)
                .type(1)
                .content("测试内容")
                .url("http://test.com/image.jpg")
                .size(1024L)
                .createTime(LocalDateTime.now())
                .build();

        testRelation = new AdMaterialRelation();
        testRelation.setId(1L);
        testRelation.setAdId(1L);
        testRelation.setMaterialId(1L);
        testRelation.setCreateTime(LocalDateTime.now());
    }

    @Test
    void testUpload() throws Exception {
        // 准备测试数据
        MultipartFile file = new MockMultipartFile(
            "test.jpg",
            "test.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );

        // 配置Mock行为
        when(ossService.upload(any(), any())).thenReturn("http://test.com/image.jpg");
        when(materialMapper.insert(any())).thenReturn(1);
        when(adConverter.toMaterialDTO(any())).thenReturn(testMaterialDTO);

        // 执行测试
        MaterialDTO result = materialService.upload(file, 1);

        // 验证结果
        assertNotNull(result);
        assertEquals("http://test.com/image.jpg", result.getUrl());
        verify(materialMapper).insert(any());
        verify(ossService).upload(any(), any());
    }

    @Test
    void testGetById() {
        // 配置Mock行为
        when(materialMapper.selectById(1L)).thenReturn(testMaterial);
        when(adConverter.toMaterialDTO(testMaterial)).thenReturn(testMaterialDTO);

        // 执行测试
        MaterialDTO result = materialService.getById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(testMaterial.getId(), result.getId());
        verify(materialMapper).selectById(1L);
    }

    @Test
    void testListByAdId() {
        // 配置Mock行为
        List<Material> materials = Arrays.asList(testMaterial);
        when(relationMapper.selectMaterialsByAdId(1L)).thenReturn(materials);
        when(adConverter.toMaterialDTO(testMaterial)).thenReturn(testMaterialDTO);

        // 执行测试
        List<MaterialDTO> results = materialService.listByAdId(1L);

        // 验证结果
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        verify(relationMapper).selectMaterialsByAdId(1L);
    }

    @Test
    void testDelete() {
        // 配置Mock行为
        when(materialMapper.selectById(1L)).thenReturn(testMaterial);

        // 执行测试
        materialService.delete(1L);

        // 验证结果
        verify(materialMapper).deleteById(1L);
        verify(ossService).delete(testMaterial.getUrl());
        verify(relationMapper).delete(any());
    }

    @Test
    void testAddMaterialToAd() {
        // 配置Mock行为
        when(materialMapper.selectById(1L)).thenReturn(testMaterial);
        when(relationMapper.selectCount(any())).thenReturn(0L);

        // 执行测试
        materialService.addMaterialToAd(1L, 1L);

        // 验证结果
        verify(relationMapper).insert(any());
    }

    @Test
    void testAddMaterialToAdWhenAlreadyExists() {
        // 配置Mock行为
        when(materialMapper.selectById(1L)).thenReturn(testMaterial);
        when(relationMapper.selectCount(any())).thenReturn(1L);

        // 执行测试
        materialService.addMaterialToAd(1L, 1L);

        // 验证结果
        verify(relationMapper, never()).insert(any());
    }

    @Test
    void testRemoveMaterialFromAd() {
        // 执行测试
        materialService.removeMaterialFromAd(1L, 1L);

        // 验证结果
        verify(relationMapper).delete(any());
    }

    @Test
    void testListAdsByMaterialId() {
        // 配置Mock行为
        List<Long> adIds = Arrays.asList(1L, 2L);
        when(relationMapper.selectAdIdsByMaterialId(1L)).thenReturn(adIds);

        // 执行测试
        List<Long> results = materialService.listAdsByMaterialId(1L);

        // 验证结果
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(relationMapper).selectAdIdsByMaterialId(1L);
    }

    @Test
    void testGetByIdWhenMaterialNotFound() {
        // 配置Mock行为
        when(materialMapper.selectById(1L)).thenReturn(null);

        // 验证异常抛出
        assertThrows(RuntimeException.class, () -> materialService.getById(1L));
    }

    @Test
    void testUploadWithEmptyFile() {
        // 准备测试数据
        MultipartFile emptyFile = new MockMultipartFile(
            "empty.jpg",
            "empty.jpg",
            "image/jpeg",
            new byte[0]
        );

        // 验证异常抛出
        assertThrows(RuntimeException.class, () -> materialService.upload(emptyFile, 1));
    }
} 