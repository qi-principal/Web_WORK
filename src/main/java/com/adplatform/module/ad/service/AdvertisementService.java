package com.adplatform.module.ad.service;

import com.adplatform.module.ad.dto.AdvertisementDTO;
import com.adplatform.module.ad.entity.Advertisement;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 广告服务接口
 *
 * @author andrew
 * @date 2023-12-19
 */
public interface AdvertisementService {

    /**
     * 创建广告
     *
     * @param dto 广告信息
     * @return 创建后的广告信息
     */
    AdvertisementDTO create(AdvertisementDTO dto);

    /**
     * 更新广告
     *
     * @param id 广告ID
     * @param dto 广告信息
     * @return 更新后的广告信息
     */
    AdvertisementDTO update(Long id, AdvertisementDTO dto);

    /**
     * 获取广告详情
     *
     * @param id 广告ID
     * @return 广告详情
     */
    AdvertisementDTO getById(Long id);

    /**
     * 分页查询广告列表
     *
     * @param page 分页参数
     * @param userId 用户ID
     * @param status 状态
     * @return 广告列表
     */
    IPage<AdvertisementDTO> page(Page<AdvertisementDTO> page, Long userId, Integer status);

    /**
     * 提交广告审核
     *
     * @param id 广告ID
     * @return 更新后的广告信息
     */
    AdvertisementDTO submit(Long id);

    /**
     * 审核通过
     *
     * @param id 广告ID
     * @return 更新后的广告信息
     */
    AdvertisementDTO approve(Long id);

    /**
     * 审核拒绝
     *
     * @param id 广告ID
     * @param reason 拒绝原因
     * @return 更新后的广告信息
     */
    AdvertisementDTO reject(Long id, String reason);

    /**
     * 删除广告
     *
     * @param id 广告ID
     */
    void delete(Long id);

    List<Advertisement> getall();
}