package com.adplatform.module.ad.service.impl;

import com.adplatform.module.ad.converter.AdConverter;
import com.adplatform.module.ad.dto.MaterialDTO;
import com.adplatform.module.ad.entity.Material;
import com.adplatform.module.ad.mapper.MaterialMapper;
import com.adplatform.module.ad.service.MaterialService;
import com.adplatform.module.ad.service.OssService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private final AdConverter adConverter;
    private final OssService ossService;

    // 素材存储目录
    private static final String MATERIAL_DIR = "materials";

    /*
    * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MaterialDTO upload(MultipartFile file, Integer type) {
        // 校验文件
        validateFile(file);

        try {
            // 上传文件到OSS
            String url = ossService.upload(file, MATERIAL_DIR);

            // 保存素材信息
            Material material = new Material();
            material.setType(type);
            material.setUrl(url);
            material.setSize(file.getSize());
            material.setCreateTime(LocalDateTime.now());

            materialMapper.insert(material);

            return adConverter.toDTO(material);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public MaterialDTO getById(Long id) {
        Material material = getMaterial(id);
        return adConverter.toDTO(material);
    }

    @Override
    public List<MaterialDTO> listByAdId(Long adId) {
        List<Material> materials = materialMapper.selectList(
            new LambdaQueryWrapper<Material>()
                .eq(Material::getAdId, adId)
        );
        return materials.stream()
                .map(adConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Material material = getMaterial(id);
        
        // 删除OSS中的文件
        if (StringUtils.hasText(material.getUrl())) {
            ossService.delete(material.getUrl());
        }
        
        materialMapper.deleteById(id);
    }


    //
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAdMaterials(Long adId, List<Long> materialIds) {
        // 先删除原有关联
        materialMapper.delete(
            new LambdaQueryWrapper<Material>()
                .eq(Material::getAdId, adId)
        );

        // 建立新的关联
        if (materialIds != null && !materialIds.isEmpty()) {
            // 使用LambdaUpdateWrapper进行批量更新
            LambdaUpdateWrapper<Material> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(Material::getId, materialIds)
                        .set(Material::getAdId, adId);
            materialMapper.update(null, updateWrapper);
        }
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