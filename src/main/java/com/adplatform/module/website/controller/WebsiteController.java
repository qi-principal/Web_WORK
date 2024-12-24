package com.adplatform.module.website.controller;

import com.adplatform.common.response.Result;
import com.adplatform.module.website.entity.Website;
import com.adplatform.module.website.service.WebsiteService;
import com.adplatform.module.website.security.WebsitePermissions;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/websites")
public class WebsiteController {

    @Autowired
    private WebsiteService websiteService;

    /**
     * 创建网站
     * 如果用户已有网站，则更新现有网站信息
     * 如果用户没有网站，则创建新网站
     * @param website
     * @return
    */
    @ApiOperation("创建网站")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "website", value = "网站", required = true, dataType = "Website", paramType = "body")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('" + WebsitePermissions.WEBSITE_CREATE + "')")
    public Result<Website> createWebsite(@RequestBody Website website) {
        // 检查用户是否已有网站
        Website existingWebsite = websiteService.getWebsiteByUserId(website.getUserId());
        
        websiteService.createWebsite(website);
        
        if (existingWebsite != null) {
            return Result.success(website);
        } else {
            return Result.success(website);
        }
    }

    /**
     * 更新网站
     * @param id
     * @param website
     * @return
    */
    @ApiOperation("更新网站")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "网站ID", required = true, dataType = "Long", paramType = "path"),
        @ApiImplicitParam(name = "website", value = "网站", required = true, dataType = "Website", paramType = "body")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.WEBSITE_UPDATE + "') and @websiteSecurityService.isWebsiteOwner(#id, principal)")
    public Result<Website> updateWebsite(@PathVariable Long id, @RequestBody Website website) {
        websiteService.updateWebsite(id, website);
        return Result.success(website);
    }

    /**
     * 获取网站
     * @param id
     * @return
    */
    @ApiOperation("获取网站")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "网站ID", required = true, dataType = "Long", paramType = "path")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.WEBSITE_READ + "') and @websiteSecurityService.canAccessWebsite(#id, principal)")
    public Result<Website> getWebsite(@PathVariable Long id) {
        Website website = websiteService.getWebsiteById(id);
        if (website == null) {
            return Result.error("网站不存在");
        }
        return Result.success(website);
    }

    /**
     * 获取网站列表
     * @param userId
     * @param status
     * @param page
     * @param size
     * @return
    */
    @ApiOperation("获取网站列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "userId", value = "用户ID", required = false, dataType = "Long", paramType = "query"),
        @ApiImplicitParam(name = "status", value = "状态", required = false, dataType = "Integer", paramType = "query"),
        @ApiImplicitParam(name = "page", value = "页码", required = false, dataType = "Integer", paramType = "query"),
        @ApiImplicitParam(name = "size", value = "页大小", required = false, dataType = "Integer", paramType = "query")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('" + WebsitePermissions.WEBSITE_READ + "')")
    public Result<List<Website>> getWebsites(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Website> websites = websiteService.getWebsites(userId, status, page, size);
        return Result.success(websites);
    }

    /**
     * 审核通过网站
     * @param id
     * @return
    */
    @ApiOperation("审核通过网站")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "网站ID", required = true, dataType = "Long", paramType = "path")
    })
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.WEBSITE_APPROVE + "')")
    public Result<Void> approveWebsite(@PathVariable Long id) {
        websiteService.approveWebsite(id);
        return Result.success();
    }

    /**
     * 审核拒绝网站
     * @param id
     * @return
    */
    @ApiOperation("拒绝网站")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "网站ID", required = true, dataType = "Long", paramType = "path")
    })
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.WEBSITE_APPROVE + "')")
    public Result<Void> rejectWebsite(@PathVariable Long id) {
        websiteService.rejectWebsite(id);
        return Result.success();
    }

    /**
     * 通过用户ID获取网站
     * @param userId
     * @return
    */
    @ApiOperation("通过用户ID获取网站")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "Long", paramType = "path")
    })
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.WEBSITE_READ + "')")
    public Result<Website> getWebsiteByUserId(@PathVariable Long userId) {
        Website website = websiteService.getWebsiteByUserId(userId);
        if (website == null) {
            return Result.error("未找到该用户的网站");
        }
        return Result.success(website);
    }
} 