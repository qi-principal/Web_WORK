package com.adplatform.module.ad.service;

import com.adplatform.module.ad.dto.AdvertisementDTO;
import com.adplatform.module.ad.enums.AdStatus;
import com.adplatform.module.ad.enums.AdType;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 广告服务测试类
 *
 * @author andrew
 * @date 2023-12-19
 */
@SpringBootTest
@Transactional
public class AdvertisementServiceTest {

    @Autowired
    private AdvertisementService advertisementService;

    /**
     * 测试创建广告
     */
    @Test
    public void testCreate() {
        AdvertisementDTO dto = createAdvertisementDTO();
        AdvertisementDTO result = advertisementService.create(dto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(dto.getTitle(), result.getTitle());
        assertEquals(AdStatus.DRAFT.getCode(), result.getStatus());
    }

    /**
     * 测试更新广告
     */
    @Test
    public void testUpdate() {
        // 先创建广告
        AdvertisementDTO dto = createAdvertisementDTO();
        AdvertisementDTO created = advertisementService.create(dto);

        // 更新广告
        created.setTitle("更新后的标题");
        AdvertisementDTO updated = advertisementService.update(created.getId(), created);

        assertNotNull(updated);
        assertEquals("更新后的标题", updated.getTitle());
    }

    /**
     * 测试获取广告详情
     */
    @Test
    public void testGetById() {
        // 先创建广告
        AdvertisementDTO dto = createAdvertisementDTO();
        AdvertisementDTO created = advertisementService.create(dto);

        // 获取详情
        AdvertisementDTO result = advertisementService.getById(created.getId());

        assertNotNull(result);
        assertEquals(created.getId(), result.getId());
        assertEquals(created.getTitle(), result.getTitle());
    }

    /**
     * 测试分页查询
     */
    @Test
    public void testPage() {
        // 先创建广告
        AdvertisementDTO dto = createAdvertisementDTO();
        advertisementService.create(dto);

        // 分页查询
        Page<AdvertisementDTO> page = new Page<>(1, 10);
        IPage<AdvertisementDTO> result = advertisementService.page(page, null, null);

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
        assertFalse(result.getRecords().isEmpty());
    }

    /**
     * 测试广告状态流转
     */
    @Test
    public void testStatusFlow() {
        // 创建广告
        AdvertisementDTO dto = createAdvertisementDTO();
        AdvertisementDTO ad = advertisementService.create(dto);
        assertEquals(AdStatus.DRAFT.getCode(), ad.getStatus());

        // 提交审核
        ad = advertisementService.submit(ad.getId());
        assertEquals(AdStatus.PENDING_REVIEW.getCode(), ad.getStatus());

        // 审核通过
        ad = advertisementService.approve(ad.getId());
        assertEquals(AdStatus.APPROVED.getCode(), ad.getStatus());

        // 开始投放
        ad = advertisementService.start(ad.getId());
        assertEquals(AdStatus.RUNNING.getCode(), ad.getStatus());

        // 暂停投放
        ad = advertisementService.pause(ad.getId());
        assertEquals(AdStatus.PAUSED.getCode(), ad.getStatus());
    }

    /**
     * 测试删除广告
     */
    @Test
    public void testDelete() {
        // 先创建广告
        AdvertisementDTO dto = createAdvertisementDTO();
        AdvertisementDTO created = advertisementService.create(dto);

        // 删除广告
        advertisementService.delete(created.getId());

        // 验证是否删除成功
        assertThrows(RuntimeException.class, () -> {
            advertisementService.getById(created.getId());
        });
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