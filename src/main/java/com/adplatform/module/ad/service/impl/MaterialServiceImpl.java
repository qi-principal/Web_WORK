package com.adplatform.module.ad.service.impl;

import com.adplatform.module.ad.converter.AdConverter;
import com.adplatform.module.ad.dto.MaterialDTO;
import com.adplatform.module.ad.entity.AdMaterialRelation;
import com.adplatform.module.ad.entity.Material;
import com.adplatform.module.ad.mapper.AdMaterialRelationMapper;
import com.adplatform.module.ad.mapper.MaterialMapper;
import com.adplatform.module.ad.service.MaterialService;
import com.adplatform.module.ad.service.OssService;
import com.adplatform.module.user.security.SecurityService;
import com.adplatform.module.ad.mapper.AdvertisementMapper;
import com.adplatform.module.ad.entity.Advertisement;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Set;

/**
 * 广告素材服务实现类
 *
 * @author andrew
 * @date 2023-12-19
 */
@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private final MaterialMapper materialMapper;
    private final AdMaterialRelationMapper relationMapper;
    private final AdConverter adConverter;
    private final OssService ossService;
    private final SecurityService securityService;
    private final AdvertisementMapper advertisementMapper;

    private static final String MATERIAL_DIR = "materials";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MaterialDTO upload(MultipartFile file, Integer type) {
        validateFile(file);

        try {
            String url = ossService.upload(file, MATERIAL_DIR);

            System.out.println("url:"+url);
            Material material = new Material();
            material.setType(type);
            material.setContent(file.getName());
            material.setUrl(url);
            material.setSize(file.getSize());
            material.setCreateTime(LocalDateTime.now());

            System.out.println("material:"+material.toString());
            materialMapper.insert(material);

            return adConverter.toMaterialDTO(material);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public MaterialDTO getById(Long id) {
        Material material = getMaterial(id);
        return adConverter.toMaterialDTO(material);
    }

    @Override
    public List<MaterialDTO> listByAdId(Long adId) {
        List<Material> materials = relationMapper.selectMaterialsByAdId(adId);
        return materials.stream()
                .map(adConverter::toMaterialDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Material material = getMaterial(id);
        
        // 删除素材关联
        LambdaQueryWrapper<AdMaterialRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdMaterialRelation::getMaterialId, id);
        relationMapper.delete(wrapper);
        
        // 删除OSS中的文件
        if (StringUtils.hasText(material.getUrl())) {
            ossService.delete(material.getUrl());
        }
        
        materialMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMaterialToAd(Long materialId, Long adId) {
        // 检查素材是否存在
        getMaterial(materialId);
        
        // 检查是否已经关联
        LambdaQueryWrapper<AdMaterialRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdMaterialRelation::getAdId, adId)
               .eq(AdMaterialRelation::getMaterialId, materialId);
        
        if (relationMapper.selectCount(wrapper) == 0) {
            AdMaterialRelation relation = new AdMaterialRelation();
            relation.setAdId(adId);
            relation.setMaterialId(materialId);
            relation.setCreateTime(LocalDateTime.now());
            relationMapper.insert(relation);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMaterialFromAd(Long materialId, Long adId) {
        LambdaQueryWrapper<AdMaterialRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdMaterialRelation::getAdId, adId)
               .eq(AdMaterialRelation::getMaterialId, materialId);
        relationMapper.delete(wrapper);
    }

    @Override
    public List<Long> listAdsByMaterialId(Long materialId) {
        return relationMapper.selectAdIdsByMaterialId(materialId);
    }

    @Override
    public List<MaterialDTO> listUserMaterials(Long userId) {
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        
        // 使用优化后的单条SQL查询
        List<Material> materials = relationMapper.selectAllMaterialsByUserId(userId);
        
        return materials.stream()
                .map(adConverter::toMaterialDTO)
                .collect(Collectors.toList());
    }

    @Override
    public IPage<MaterialDTO> pageUserMaterials(Page<MaterialDTO> dtoPage, Long userId) {
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }

        // 创建实体分页对象
        Page<Material> entityPage = new Page<>(dtoPage.getCurrent(), dtoPage.getSize());
        
        // 使用优化后的分页查询
        IPage<Material> materialPage = relationMapper.selectMaterialsByUserId(entityPage, userId);
        
        // 转换结果
        return materialPage.convert(adConverter::toMaterialDTO);
    }

    /**
     * 获取素材信息
     */
    private Material getMaterial(Long id) {
        Material material = materialMapper.selectById(id);
        if (material == null) {
            throw new RuntimeException("素材不存在");
        }
        return material;
    }

    /**
     * 校验文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }
        
        // TODO: 添加文件大小限制
        // if (file.getSize() > MAX_FILE_SIZE) {
        //     throw new RuntimeException("文件大小超过限制");
        // }
        
        // TODO: 添加文件类型限制
        // String contentType = file.getContentType();
        // if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
        //     throw new RuntimeException("不支持的文件类型");
        // }
    }
} 