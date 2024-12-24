package com.adplatform.module.ad.service;

import com.adplatform.module.ad.dto.MaterialDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 广告素材服务接口
 *
 * @author andrew
 * @date 2023-12-19
 */
public interface MaterialService {

    /**
     * 上传素材
     *
     * @param file 文件
     * @param type 素材类型
     * @return 素材信息
     */
    MaterialDTO upload(MultipartFile file, Integer type);

    /**
     * 获取素材详情
     *
     * @param id 素材ID
     * @return 素材信息
     */
    MaterialDTO getById(Long id);

    /**
     * 获取广告的素材列表
     *
     * @param adId 广告ID
     * @return 素材列表
     */
    List<MaterialDTO> listByAdId(Long adId);

    /**
     * 删除素材
     *
     * @param id 素材ID
     */
    void delete(Long id);

    /**
     * 添加素材到广告
     *
     * @param materialId 素材ID
     * @param adId 广告ID
     */
    void addMaterialToAd(Long materialId, Long adId);

    /**
     * 从广告中移除素材
     *
     * @param materialId 素材ID
     * @param adId 广告ID
     */
    void removeMaterialFromAd(Long materialId, Long adId);

    /**
     * 获取使用该素材的广告ID列表
     *
     * @param materialId 素材ID
     * @return 广告ID列表
     */
    List<Long> listAdsByMaterialId(Long materialId);

    /**
     * 获取指定用户的所有素材列表
     *
     * @param userId 用户ID
     * @return 素材列表
     */
    List<MaterialDTO> listUserMaterials(Long userId);

    /**
     * 分页获取指定用户的素材列表
     *
     * @param page 分页参数
     * @param userId 用户ID
     * @return 分页后的素材列表
     */
    IPage<MaterialDTO> pageUserMaterials(Page<MaterialDTO> page, Long userId);
} 