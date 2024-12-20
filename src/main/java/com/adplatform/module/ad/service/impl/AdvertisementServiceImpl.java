package com.adplatform.module.ad.service.impl;

import com.adplatform.module.ad.converter.AdConverter;
import com.adplatform.module.ad.dto.AdvertisementDTO;
import com.adplatform.module.ad.entity.Advertisement;
import com.adplatform.module.ad.enums.AdStatus;
import com.adplatform.module.ad.mapper.AdvertisementMapper;
import com.adplatform.module.ad.mapper.MaterialMapper;
import com.adplatform.module.ad.service.AdvertisementService;
import com.adplatform.module.user.security.SecurityService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>广告服务实现类</p>
 * 
 * @author andrew
 * @date 2023-12-19
 */
@Service
@RequiredArgsConstructor
public class AdvertisementServiceImpl implements AdvertisementService {

    private final AdvertisementMapper advertisementMapper;
    private final MaterialMapper materialMapper;
    private final AdConverter adConverter;
    private final SecurityService securityService;

    
    
    /**
     * 创建广告
     * 
     * @param dto 广告数据传输对象
     * @return 创建后的广告DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdvertisementDTO create(AdvertisementDTO dto) {
        // 校验预算
        validateBudget(dto);
        
        // 创建广告
        Advertisement advertisement = new Advertisement();
        copyProperties(dto, advertisement);
        
        // 自动设置当前登录用户ID
        Long currentUserId = securityService.getCurrentUserId();
        advertisement.setUserId(currentUserId);
        
        advertisement.setStatus(AdStatus.DRAFT.getCode());
        advertisement.setCreateTime(LocalDateTime.now());
        advertisement.setUpdateTime(LocalDateTime.now());

        System.out.println("当前用户ID: " + currentUserId);
        System.out.println(advertisement.toString());
        
        advertisementMapper.insert(advertisement);
        
        return adConverter.toAdvertisementDTO(advertisement);
    }

    /**
     * 更新广告信息
     * 
     * @param id 广告ID
     * @param dto 广告数据传输对象
     * @return 更新后的广告DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdvertisementDTO update(Long id, AdvertisementDTO dto) {
        // 获取广告
        Advertisement advertisement = getAdvertisement(id);
        
        // 校验状态
        validateStatus(advertisement, AdStatus.DRAFT);
        
        // 校验预算
        validateBudget(dto);
        
        // 更新广告
        copyProperties(dto, advertisement);
        advertisement.setUpdateTime(LocalDateTime.now());
        
        advertisementMapper.updateById(advertisement);
        
        return adConverter.toAdvertisementDTO(advertisement);
    }

    /**
     * 根据ID获取广告信息
     * 
     * @param id 广告ID
     * @return 广告DTO
     */
    @Override
    public AdvertisementDTO getById(Long id) {
        Advertisement advertisement = getAdvertisement(id);
        return adConverter.toAdvertisementDTO(advertisement);
    }

    /**
     * 分页查询广告列表
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @param status 广告状态
     * @return 广告DTO分页结果
     */
    @Override
    public IPage<AdvertisementDTO> page(Page<AdvertisementDTO> page, Long userId, Integer status) {
        Page<Advertisement> entityPage = new Page<>(page.getCurrent(), page.getSize());
        
        LambdaQueryWrapper<Advertisement> wrapper = new LambdaQueryWrapper<Advertisement>()
                .eq(userId != null, Advertisement::getUserId, userId)
                .eq(status != null, Advertisement::getStatus, status)
                .orderByDesc(Advertisement::getCreateTime);
        
        IPage<Advertisement> entityResult = advertisementMapper.selectPage(entityPage, wrapper);
        
        return entityResult.convert(adConverter::toAdvertisementDTO);
    }

    /**
     * 提交广告审核
     * 
     * @param id 广告ID
     * @return 更新后的广告DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdvertisementDTO submit(Long id) {
        Advertisement advertisement = getAdvertisement(id);
        validateStatus(advertisement, AdStatus.DRAFT);
        
        advertisement.setStatus(AdStatus.PENDING_REVIEW.getCode());
        advertisement.setUpdateTime(LocalDateTime.now());
        
        advertisementMapper.updateById(advertisement);
        
        return adConverter.toAdvertisementDTO(advertisement);
    }

    /**
     * 审核通过广告
     * 
     * @param id 广告ID
     * @return 更新后的广告DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdvertisementDTO approve(Long id) {
        Advertisement advertisement = getAdvertisement(id);
        validateStatus(advertisement, AdStatus.PENDING_REVIEW);
        
        advertisement.setStatus(AdStatus.APPROVED.getCode());
        advertisement.setUpdateTime(LocalDateTime.now());
        
        advertisementMapper.updateById(advertisement);
        
        return adConverter.toAdvertisementDTO(advertisement);
    }

    /**
     * 拒绝广告
     * 
     * @param id 广告ID
     * @param reason 拒绝原因
     * @return 更新后的广告DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdvertisementDTO reject(Long id, String reason) {
        Advertisement advertisement = getAdvertisement(id);
        validateStatus(advertisement, AdStatus.PENDING_REVIEW);
        
        advertisement.setStatus(AdStatus.REJECTED.getCode());
        advertisement.setUpdateTime(LocalDateTime.now());
        
        advertisementMapper.updateById(advertisement);
        
        return adConverter.toAdvertisementDTO(advertisement);
    }

    /**
     * 启动广告投放
     * 
     * @param id 广告ID
     * @return 更新后的广告DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdvertisementDTO start(Long id) {
        Advertisement advertisement = getAdvertisement(id);
        validateStatus(advertisement, AdStatus.APPROVED, AdStatus.PAUSED);
        
        advertisement.setStatus(AdStatus.RUNNING.getCode());
        advertisement.setUpdateTime(LocalDateTime.now());
        
        advertisementMapper.updateById(advertisement);
        
        return adConverter.toAdvertisementDTO(advertisement);
    }

    /**
     * 暂停广告投放
     * 
     * @param id 广告ID
     * @return 更新后的广告DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdvertisementDTO pause(Long id) {
        Advertisement advertisement = getAdvertisement(id);
        validateStatus(advertisement, AdStatus.RUNNING);
        
        advertisement.setStatus(AdStatus.PAUSED.getCode());
        advertisement.setUpdateTime(LocalDateTime.now());
        
        advertisementMapper.updateById(advertisement);
        
        return adConverter.toAdvertisementDTO(advertisement);
    }

    /**
     * 删除广告
     * 
     * @param id 广告ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Advertisement advertisement = getAdvertisement(id);
        validateStatus(advertisement, AdStatus.DRAFT, AdStatus.REJECTED);

        advertisementMapper.deleteById(id);
    }

    /**
     * 获取广告信息
     * 
     * @param id 广告ID
     * @return 广告实体
     */
    private Advertisement getAdvertisement(Long id) {
        Advertisement advertisement = advertisementMapper.selectById(id);
        if (advertisement == null) {
            throw new RuntimeException("广告不存在");
        }
        return advertisement;
    }

    /**
     * 校验广告状态
     * 
     * @param advertisement 广告实体
     * @param allowedStatus 允许的状态
     */
    private void validateStatus(Advertisement advertisement, AdStatus... allowedStatus) {
        boolean valid = false;
        for (AdStatus status : allowedStatus) {
            if (status.getCode().equals(advertisement.getStatus())) {
                valid = true;
                break;
            }
        }
        if (!valid) {
            throw new RuntimeException("广告状态不正确");
        }
    }

    /**
     * 校验预算
     * 
     * @param dto 广告数据传输对象
     */
    private void validateBudget(AdvertisementDTO dto) {
        if (dto.getBudget().compareTo(dto.getDailyBudget()) < 0) {
            throw new RuntimeException("总预算不能小于日预算");
        }
    }

    /**
     * 复制属性
     * 
     * @param dto 广告数据传输对象
     * @param advertisement 广告实体
     */
    private void copyProperties(AdvertisementDTO dto, Advertisement advertisement) {
        System.out.println("复制属性");
        advertisement.setTitle(dto.getTitle());
        advertisement.setDescription(dto.getDescription());
        advertisement.setType(dto.getType());
        advertisement.setBudget(dto.getBudget());
        advertisement.setDailyBudget(dto.getDailyBudget());
        advertisement.setStartTime(dto.getStartTime());
        advertisement.setEndTime(dto.getEndTime());
        advertisement.setUserId(dto.getUserId());
    }
} 