package com.adplatform.module.ad.service.impl;

import com.adplatform.module.ad.converter.AdConverter;
import com.adplatform.module.ad.dto.AdvertisementDTO;
import com.adplatform.module.ad.entity.AdMaterialRelation;
import com.adplatform.module.ad.entity.Advertisement;
import com.adplatform.module.ad.enums.AdStatus;
import com.adplatform.module.ad.mapper.AdMaterialRelationMapper;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
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
    private final AdMaterialRelationMapper adMaterialRelationMapper;
    private final AdConverter adConverter;
    private final SecurityService securityService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdvertisementDTO create(AdvertisementDTO dto) {
        // 校验预算
        validateBudget(dto);
        // 校验点击链接
        validateClickUrl(dto.getClickUrl());
        
        // 创建广告
        Advertisement advertisement = new Advertisement();
        copyProperties(dto, advertisement);

        // 自动设置当前登录用户ID
        Long currentUserId = securityService.getCurrentUserId();
        advertisement.setUserId(currentUserId);
        advertisement.setStatus(AdStatus.DRAFT.getCode());
        advertisement.setCreateTime(LocalDateTime.now());
        advertisement.setUpdateTime(LocalDateTime.now());

        System.out.println("===========================================================");
        System.out.println("advertisement"+advertisement);
        System.out.println("===========================================================");
        advertisementMapper.insert(advertisement);

        // 处理广告素材关联
        if (!CollectionUtils.isEmpty(dto.getMaterialIds())) {
            for (Long materialId : dto.getMaterialIds()) {
                AdMaterialRelation relation = new AdMaterialRelation();
                relation.setAdId(advertisement.getId());
                relation.setMaterialId(materialId);
                relation.setCreateTime(LocalDateTime.now());
                adMaterialRelationMapper.insert(relation);
            }
        }
        
        return adConverter.toAdvertisementDTO(advertisement);
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
        // 校验点击链接
        validateClickUrl(dto.getClickUrl());
        
        // 更新广告
        copyProperties(dto, advertisement);
        advertisement.setUpdateTime(LocalDateTime.now());
        
        advertisementMapper.updateById(advertisement);

        // 更新广告素材关联
        if (!CollectionUtils.isEmpty(dto.getMaterialIds())) {
            // 删除旧的关联关系
            adMaterialRelationMapper.delete(
                new LambdaQueryWrapper<AdMaterialRelation>()
                    .eq(AdMaterialRelation::getAdId, id)
            );
            
            // 创建新的关联关系
            for (Long materialId : dto.getMaterialIds()) {
                AdMaterialRelation relation = new AdMaterialRelation();
                relation.setAdId(id);
                relation.setMaterialId(materialId);
                relation.setCreateTime(LocalDateTime.now());
                adMaterialRelationMapper.insert(relation);
            }
        }
        
        return adConverter.toAdvertisementDTO(advertisement);
    }

    @Override
    public AdvertisementDTO getById(Long id) {
        Advertisement advertisement = getAdvertisement(id);
        return adConverter.toAdvertisementDTO(advertisement);
    }

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Advertisement advertisement = getAdvertisement(id);
        validateStatus(advertisement, AdStatus.DRAFT, AdStatus.REJECTED);
        advertisementMapper.deleteById(id);
    }

    private Advertisement getAdvertisement(Long id) {
        Advertisement advertisement = advertisementMapper.selectById(id);
        if (advertisement == null) {
            throw new RuntimeException("广告不存在");
        }
        return advertisement;
    }

    private void validateStatus(Advertisement advertisement, AdStatus... allowedStatus) {
        boolean valid = false;
        for (AdStatus status : allowedStatus) {
            if (status.getCode().equals(advertisement.getStatus())) {
                valid = true;
                break;
            }
        }
        if (!valid) {
            throw new RuntimeException("当前状态不允许此操作");
        }
    }

    private void validateBudget(AdvertisementDTO dto) {
        if (dto.getBudget() == null || dto.getBudget().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("总预算必须大于0");
        }
        if (dto.getDailyBudget() == null || dto.getDailyBudget().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("日预算必须大于0");
        }
        if (dto.getDailyBudget().compareTo(dto.getBudget()) > 0) {
            throw new RuntimeException("日预算不能大于总预算");
        }
    }

    private void validateClickUrl(String clickUrl) {
        if (!StringUtils.hasText(clickUrl)) {
            throw new RuntimeException("点击链接不能为空");
        }
        // TODO: 可以添加更多的URL格式验证
    }

    private void copyProperties(AdvertisementDTO source, Advertisement target) {
        target.setTitle(source.getTitle());
        target.setDescription(source.getDescription());
        target.setType(source.getType());
        target.setBudget(source.getBudget());
        target.setDailyBudget(source.getDailyBudget());
        target.setStartTime(source.getStartTime());
        target.setEndTime(source.getEndTime());
        target.setClickUrl(source.getClickUrl());
    }
} 