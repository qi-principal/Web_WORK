package com.adplatform.module.ad.service;

import com.adplatform.module.ad.dto.MaterialDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * 保存广告素材关联
     *
     * @param adId 广告ID
     * @param materialIds 素材ID列表
     */
    void saveAdMaterials(Long adId, List<Long> materialIds);

} 