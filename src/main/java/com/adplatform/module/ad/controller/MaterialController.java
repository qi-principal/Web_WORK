package com.adplatform.module.ad.controller;

import com.adplatform.common.response.Result;
import com.adplatform.module.ad.dto.MaterialDTO;
import com.adplatform.module.ad.service.MaterialService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    public Result<MaterialDTO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") Integer type) {
        System.out.println("controller upload"+file);
        return Result.success(materialService.upload(file, type));
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
    public Result<MaterialDTO> getById(@PathVariable Long id) {
        return Result.success(materialService.getById(id));
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
    public Result<List<MaterialDTO>> listByAdId(@PathVariable Long adId) {
        return Result.success(materialService.listByAdId(adId));
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
    public Result<Void> delete(@PathVariable Long id) {
        materialService.delete(id);
        return Result.success();
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
    public Result<Void> addMaterialToAd(
            @PathVariable Long materialId,
            @PathVariable Long adId) {
        materialService.addMaterialToAd(materialId, adId);
        return Result.success();
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
    public Result<Void> removeMaterialFromAd(
            @PathVariable Long materialId,
            @PathVariable Long adId) {
        materialService.removeMaterialFromAd(materialId, adId);
        return Result.success();
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
    public Result<List<Long>> listAdsByMaterialId(@PathVariable Long materialId) {
        return Result.success(materialService.listAdsByMaterialId(materialId));
    }

    /**
     * 获取指定用户的所有素材列表
     */
    @ApiOperation("获取指定用户的所有素材列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 400, message = "参数错误")
    })
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADVERTISER')")
    public Result<List<MaterialDTO>> listUserMaterials(@PathVariable Long userId) {
        return Result.success(materialService.listUserMaterials(userId));
    }

    /**
     * 分页获取指定用户的素材列表
     */
    @ApiOperation("分页获取指定用户的素材列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 400, message = "参数错误")
    })
    @GetMapping("/user/{userId}/page")
    @PreAuthorize("hasRole('ADVERTISER')")
    public Result<IPage<MaterialDTO>> pageUserMaterials(
            @PathVariable Long userId,
            @ApiParam(value = "页码", defaultValue = "1")
            @RequestParam(defaultValue = "1") Long current,
            @ApiParam(value = "每页数量", defaultValue = "10")
            @RequestParam(defaultValue = "10") Long size) {
        Page<MaterialDTO> page = new Page<>(current, size);
        return Result.success(materialService.pageUserMaterials(page, userId));
    }
} 