package com.adplatform.module.ad.service.impl;

import com.adplatform.module.ad.converter.AdConverter;
import com.adplatform.module.ad.dto.AdvertisementDTO;
import com.adplatform.module.ad.entity.Advertisement;
import com.adplatform.module.ad.enums.AdStatus;
import com.adplatform.module.ad.mapper.AdvertisementMapper;
import com.adplatform.module.ad.mapper.MaterialMapper;
import com.adplatform.module.ad.service.AdvertisementService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 广告服务实现类
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdvertisementDTO create(AdvertisementDTO dto) {
        // 校验预算
        validateBudget(dto);
        
        // 创建广告
        Advertisement advertisement = new Advertisement();
        copyProperties(dto, advertisement);
        advertisement.setStatus(AdStatus.DRAFT.getCode());
        advertisement.setCreateTime(LocalDateTime.now());
        advertisement.setUpdateTime(LocalDateTime.now());
        
        advertisementMapper.insert(advertisement);
        
        return adConverter.toDTO(advertisement);
    }

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
        
        return adConverter.toDTO(advertisement);
    }

    @Override
    public AdvertisementDTO getById(Long id) {
        Advertisement advertisement = getAdvertisement(id);
        return adConverter.toDTO(advertisement);
    }

    @Override
    public IPage<AdvertisementDTO> page(Page<AdvertisementDTO> page, Long userId, Integer status) {
        Page<Advertisement> entityPage = new Page<>(page.getCurrent(), page.getSize());
        
        LambdaQueryWrapper<Advertisement> wrapper = new LambdaQueryWrapper<Advertisement>()
                .eq(userId != null, Advertisement::getUserId, userId)
                .eq(status != null, Advertisement::getStatus, status)
                .orderByDesc(Advertisement::getCreateTime);
        
        IPage<Advertisement> entityResult = advertisementMapper.selectPage(entityPage, wrapper);
        
        return entityResult.convert(adConverter::toDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdvertisementDTO submit(Long id) {
        Advertisement advertisement = getAdvertisement(id);
        validateStatus(advertisement, AdStatus.DRAFT);
        
        advertisement.setStatus(AdStatus.PENDING_REVIEW.getCode());
        advertisement.setUpdateTime(LocalDateTime.now());
        
        advertisementMapper.updateById(advertisement);
        
        return adConverter.toDTO(advertisement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdvertisementDTO approve(Long id) {
        Advertisement advertisement = getAdvertisement(id);
        validateStatus(advertisement, AdStatus.PENDING_REVIEW);
        
        advertisement.setStatus(AdStatus.APPROVED.getCode());
        advertisement.setUpdateTime(LocalDateTime.now());
        
        advertisementMapper.updateById(advertisement);
        
        return adConverter.toDTO(advertisement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdvertisementDTO reject(Long id, String reason) {
        Advertisement advertisement = getAdvertisement(id);
        validateStatus(advertisement, AdStatus.PENDING_REVIEW);
        
        advertisement.setStatus(AdStatus.REJECTED.getCode());
        advertisement.setUpdateTime(LocalDateTime.now());
        
        advertisementMapper.updateById(advertisement);
        
        return adConverter.toDTO(advertisement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdvertisementDTO start(Long id) {
        Advertisement advertisement = getAdvertisement(id);
        validateStatus(advertisement, AdStatus.APPROVED, AdStatus.PAUSED);
        
        advertisement.setStatus(AdStatus.RUNNING.getCode());
        advertisement.setUpdateTime(LocalDateTime.now());
        
        advertisementMapper.updateById(advertisement);
        
        return adConverter.toDTO(advertisement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdvertisementDTO pause(Long id) {
        Advertisement advertisement = getAdvertisement(id);
        validateStatus(advertisement, AdStatus.RUNNING);
        
        advertisement.setStatus(AdStatus.PAUSED.getCode());
        advertisement.setUpdateTime(LocalDateTime.now());
        
        advertisementMapper.updateById(advertisement);
        
        return adConverter.toDTO(advertisement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Advertisement advertisement = getAdvertisement(id);
        validateStatus(advertisement, AdStatus.DRAFT, AdStatus.REJECTED);
        
        advertisementMapper.deleteById(id);
    }

    /**
     * 获取广告信息
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
     */
    private void validateBudget(AdvertisementDTO dto) {
        if (dto.getBudget().compareTo(dto.getDailyBudget()) < 0) {
            throw new RuntimeException("总预算不能小于日预算");
        }
    }

    /**
     * 复制属性
     */
    private void copyProperties(AdvertisementDTO dto, Advertisement advertisement) {
        advertisement.setTitle(dto.getTitle());
        advertisement.setDescription(dto.getDescription());
        advertisement.setType(dto.getType());
        advertisement.setBudget(dto.getBudget());
        advertisement.setDailyBudget(dto.getDailyBudget());
        advertisement.setStartTime(dto.getStartTime());
        advertisement.setEndTime(dto.getEndTime());
    }
} 