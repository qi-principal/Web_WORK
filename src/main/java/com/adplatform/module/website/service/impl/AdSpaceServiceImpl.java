package com.adplatform.module.website.service.impl;

import com.adplatform.module.website.entity.AdSpace;
import com.adplatform.module.website.mapper.AdSpaceMapper;
import com.adplatform.module.website.service.AdSpaceService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class AdSpaceServiceImpl extends ServiceImpl<AdSpaceMapper, AdSpace> implements AdSpaceService {

    @Override
    @Transactional
    public void createAdSpace(Long websiteId, AdSpace adSpace) {
        adSpace.setWebsiteId(websiteId);
        adSpace.setStatus(0); // 待审核
        this.save(adSpace);
        // 生成并设置广告代码
        adSpace.setCode(generateAdCode(adSpace.getId()));
        this.updateById(adSpace);
    }

    @Override
    @Transactional
    public void updateAdSpace(Long id, AdSpace adSpace) {
        AdSpace existing = this.getById(id);
        if (existing != null) {
            existing.setName(adSpace.getName());
            existing.setWidth(adSpace.getWidth());
            existing.setHeight(adSpace.getHeight());
            existing.setStatus(0); // 更新后重新审核
            this.updateById(existing);
        }
    }

    @Override
    public AdSpace getAdSpaceById(Long id) {
        return this.getById(id);
    }

    @Override
    public List<AdSpace> getAdSpaces(Long websiteId, Integer status, int page, int size) {
        Page<AdSpace> pageParam = new Page<>(page, size);
        return this.lambdaQuery()
                .eq(websiteId != null, AdSpace::getWebsiteId, websiteId)
                .eq(status != null, AdSpace::getStatus, status)
                .page(pageParam)
                .getRecords();
    }

    @Override
    @Transactional
    public void approveAdSpace(Long id) {
        AdSpace adSpace = this.getById(id);
        if (adSpace != null) {
            adSpace.setStatus(1); // 已审核
            this.updateById(adSpace);
        }
    }

    @Override
    @Transactional
    public void rejectAdSpace(Long id) {
        AdSpace adSpace = this.getById(id);
        if (adSpace != null) {
            adSpace.setStatus(2); // 已拒绝
            this.updateById(adSpace);
        }
    }

    @Override
    public String generateAdCode(Long adSpaceId) {
        // 生成唯一的广告展示页面URL
        String uniquePath = UUID.randomUUID().toString();
        // 假设广告展示页面的基础URL为：http://yourdomain.com/ad/display/{uniquePath}
        String adDisplayUrl = "http://yourdomain.com/ad/display/" + uniquePath;
        return "<iframe src=\"" + adDisplayUrl + "\" frameborder=\"0\"></iframe>";
    }
} 