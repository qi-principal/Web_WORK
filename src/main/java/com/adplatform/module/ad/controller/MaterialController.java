package com.adplatform.module.ad.controller;

import com.adplatform.module.ad.dto.MaterialDTO;
import com.adplatform.module.ad.service.MaterialService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 广告素材控制器
 *
 * @author andrew
 * @date 2023-12-19
 */
@Api(tags = "广告素材管理")
@RestController
@RequestMapping("/v1/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    /**
     * 上传素材
     */
    @ApiOperation("上传素材")
    @ApiResponses({
        @ApiResponse(code = 200, message = "上传成功"),
        @ApiResponse(code = 400, message = "参数错误")
    })
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADVERTISER')")
    public MaterialDTO upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") Integer type) {
        return materialService.upload(file, type);
    }

    /**
     * 获取素材详情
     */
    @ApiOperation("获取素材详情")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 404, message = "素材不存在")
    })
    @GetMapping("/{id}")
    public MaterialDTO getById(@PathVariable Long id) {
        return materialService.getById(id);
    }

    /**
     * 获取广告的素材列表
     */
    @ApiOperation("获取广告的素材列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 404, message = "广告不存在")
    })
    @GetMapping("/ad/{adId}")
    public List<MaterialDTO> listByAdId(@PathVariable Long adId) {
        return materialService.listByAdId(adId);
    }

    /**
     * 删除素材
     */
    @ApiOperation("删除素材")
    @ApiResponses({
        @ApiResponse(code = 200, message = "删除成功"),
        @ApiResponse(code = 404, message = "素材不存在")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADVERTISER')")
    public void delete(@PathVariable Long id) {
        materialService.delete(id);
    }

    /**
     * 添加素材到广告
     */
    @ApiOperation("添加素材到广告")
    @ApiResponses({
        @ApiResponse(code = 200, message = "添加成功"),
        @ApiResponse(code = 404, message = "广告或素材不存在")
    })
    @PostMapping("/{materialId}/ads/{adId}")
    @PreAuthorize("hasRole('ADVERTISER')")
    public void addMaterialToAd(
            @PathVariable Long materialId,
            @PathVariable Long adId) {
        materialService.addMaterialToAd(materialId, adId);
    }

    /**
     * 从广告中移除素材
     */
    @ApiOperation("从广告中移除素材")
    @ApiResponses({
        @ApiResponse(code = 200, message = "移除成功"),
        @ApiResponse(code = 404, message = "关联不存在")
    })
    @DeleteMapping("/{materialId}/ads/{adId}")
    @PreAuthorize("hasRole('ADVERTISER')")
    public void removeMaterialFromAd(
            @PathVariable Long materialId,
            @PathVariable Long adId) {
        materialService.removeMaterialFromAd(materialId, adId);
    }

    /**
     * 获取使用该素材的广告列表
     */
    @ApiOperation("获取使用该素材的广告列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 404, message = "素材不存在")
    })
    @GetMapping("/{materialId}/ads")
    public List<Long> listAdsByMaterialId(@PathVariable Long materialId) {
        return materialService.listAdsByMaterialId(materialId);
    }
} 