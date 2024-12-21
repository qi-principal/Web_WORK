package com.adplatform.module.website.service;

import com.adplatform.module.website.entity.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface PageService extends IService<Page> {
    /**
     * 创建页面
     */
    void createPage(Long adSpaceId, Page page);

    /**
     * 更新页面信息
     */
    void updatePage(Long id, Page page);

    /**
     * 获取页面详情
     */
    Page getPageById(Long id);

    /**
     * 获取页面列表
     */
    List<Page> getPages(Long adSpaceId, Integer status, int page, int size);

    /**
     * 审核通过页面
     */
    void approvePage(Long id);

    /**
     * 审核拒绝页面
     */
    void rejectPage(Long id);
} 