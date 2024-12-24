package com.adplatform.module.website.controller;

import com.adplatform.common.response.Result;
import com.adplatform.module.website.entity.AdSpace;
import com.adplatform.module.website.service.AdSpaceService;
import com.adplatform.module.website.security.WebsitePermissions;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class AdSpaceController {

    @Autowired
    private AdSpaceService adSpaceService;

    /**
     * 创建广告位
     * @param websiteId
     * @param adSpace
     * @return
    */
    @ApiOperation("创建广告位")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "websiteId", value = "网站ID", required = true, dataType = "Long", paramType = "path"),
        @ApiImplicitParam(name = "adSpace", value = "广告位", required = true, dataType = "AdSpace", paramType = "body")
    })
    @PostMapping("/websites/{websiteId}/spaces")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.AD_SPACE_CREATE + "')")
    public Result<AdSpace> createAdSpace(@PathVariable Long websiteId, @RequestBody AdSpace adSpace) {
        adSpaceService.createAdSpace(websiteId, adSpace);
        return Result.success(adSpace);
    }

    /**
     * 更新广告位
     * @param id
     * @param adSpace
     * @return
    */
    @ApiOperation("更新广告位")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "广告位ID", required = true, dataType = "Long", paramType = "path"),
        @ApiImplicitParam(name = "adSpace", value = "广告位", required = true, dataType = "AdSpace", paramType = "body")
    })
    @PutMapping("/spaces/{id}")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.AD_SPACE_UPDATE + "') and @websiteSecurityService.isAdSpaceOwner(#id, principal)")
    public Result<AdSpace> updateAdSpace(@PathVariable Long id, @RequestBody AdSpace adSpace) {
        adSpaceService.updateAdSpace(id, adSpace);
        AdSpace updatedAdSpace = adSpaceService.getAdSpaceById(id);
        return Result.success(updatedAdSpace);
    }

    /**
     * 获取广告位
     * @param id
     * @return
    */
    @ApiOperation("获取广告位")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "广告位ID", required = true, dataType = "Long", paramType = "path")
    })
    @GetMapping("/spaces/{id}")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.AD_SPACE_READ + "') ")
    public Result<AdSpace> getAdSpace(@PathVariable Long id) {
        AdSpace adSpace = adSpaceService.getAdSpaceById(id);
        if (adSpace == null) {
            return Result.error("广告位不存在");
        }
        return Result.success(adSpace);
    }

    /**
     * 获取广告位列表
     * @param websiteId
     * @param status
     * @param page
     * @param size
     * @return
    */
    @ApiOperation("获取广告位列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "websiteId", value = "网站ID", required = true, dataType = "Long", paramType = "path"),
        @ApiImplicitParam(name = "status", value = "状态", required = false, dataType = "Integer", paramType = "query"),
        @ApiImplicitParam(name = "page", value = "页码", required = false, dataType = "Integer", paramType = "query"),
        @ApiImplicitParam(name = "size", value = "页大小", required = false, dataType = "Integer", paramType = "query")
    })
    @GetMapping("/websites/{websiteId}/spaces")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.AD_SPACE_READ + "')")
    public Result<List<AdSpace>> getAdSpaces(
            @PathVariable Long websiteId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<AdSpace> adSpaces = adSpaceService.getAdSpaces(websiteId, status, page, size);
        return Result.success(adSpaces);
    }

    /**
     * 获取广告位代码
     * @param id
     * @return
    */
    @ApiOperation("获取广告位代码")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "广告位ID", required = true, dataType = "Long", paramType = "path")
    })
    @GetMapping("/spaces/{id}/code")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.AD_SPACE_READ + "') and @websiteSecurityService.canAccessAdSpace(#id, principal)")
    public Result<String> getAdCode(@PathVariable Long id) {
        AdSpace adSpace = adSpaceService.getAdSpaceById(id);
        if (adSpace == null) {
            return Result.error("广告位不存在");
        }
        return Result.success(adSpace.getCode());
    }

    /**
     * 审核通过广告位
     * @param id
     * @return
    */
    @ApiOperation("审核通过广告位")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "广告位ID", required = true, dataType = "Long", paramType = "path")
    })
    @PostMapping("/spaces/{id}/approve")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.AD_SPACE_APPROVE + "')")
    public Result<AdSpace> approveAdSpace(@PathVariable Long id) {
        adSpaceService.approveAdSpace(id);
        AdSpace adSpace = adSpaceService.getAdSpaceById(id);
        return Result.success(adSpace);
    }

    /**
     * 拒绝广告位
     * @param id
     * @return
    */  
    @ApiOperation("拒绝广告位")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "广告位ID", required = true, dataType = "Long", paramType = "path")
    })
    @PostMapping("/spaces/{id}/reject")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.AD_SPACE_APPROVE + "')")
    public Result<Void> rejectAdSpace(@PathVariable Long id) {
        adSpaceService.rejectAdSpace(id);
        return Result.success();
    }
} 