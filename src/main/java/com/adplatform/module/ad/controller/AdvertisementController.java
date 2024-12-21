//package com.adplatform.module.ad.controller;
//
//import com.adplatform.module.ad.dto.AdvertisementDTO;
//import com.adplatform.module.ad.service.AdvertisementService;
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import io.swagger.annotations.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import javax.validation.Valid;
//
///**
// * 广告控制器
// *
// * @author andrew
// * @date 2023-12-19
// */
//@Api(tags = "广告管理接口")
//@RestController
//@RequestMapping("/v1/ads")
//@RequiredArgsConstructor
//public class AdvertisementController {
//
//    private final AdvertisementService advertisementService;
//
//    /**
//     * 创建广告
//     */
//    @ApiOperation("创建广告")
//    @ApiResponses({
//        @ApiResponse(code = 200, message = "创建成功"),
//        @ApiResponse(code = 400, message = "请求参数错误")
//    })
//    @PostMapping
//    @PreAuthorize("hasRole('ADVERTISER')")
//    public AdvertisementDTO create(@Valid @RequestBody AdvertisementDTO dto) {
//        return advertisementService.create(dto);
//    }
//
//    /**
//     * 更新广告
//     */
//    @ApiOperation("更新广告")
//    @ApiResponses({
//        @ApiResponse(code = 200, message = "更新成功"),
//        @ApiResponse(code = 404, message = "广告不存在")
//    })
//    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADVERTISER')")
//    public AdvertisementDTO update(@PathVariable Long id, @Valid @RequestBody AdvertisementDTO dto) {
//        return advertisementService.update(id, dto);
//    }
//
//    /**
//     * 获取广告详情
//     */
//    @ApiOperation("获取广告详情")
//    @ApiResponses({
//        @ApiResponse(code = 200, message = "获取广告详情成功"),
//        @ApiResponse(code = 404, message = "广告不存在")
//    })
//    @GetMapping("/{id}")
//    public AdvertisementDTO getById(@PathVariable Long id) {
//        return advertisementService.getById(id);
//    }
//
//    /**
//     * 分页查询广告列表
//     */
//    @ApiOperation("获取广告列表")
//    @ApiResponses({
//        @ApiResponse(code = 200, message = "获取广告列表成功"),
//        @ApiResponse(code = 400, message = "请求参数错误")
//    })
//    @GetMapping
//    public IPage<AdvertisementDTO> page(
//            @ApiParam(value = "页码", defaultValue = "1")
//            @RequestParam(defaultValue = "1") Integer current,
//            @ApiParam(value = "每页数量", defaultValue = "10")
//            @RequestParam(defaultValue = "10") Integer size,
//            @ApiParam(value = "广告状态")
//            @RequestParam(required = false) Long userId,
//            @ApiParam(value = "广告状态")
//            @RequestParam(required = false) Integer status) {
//        return advertisementService.page(new Page<>(current, size), userId, status);
//    }
//
//    /**
//     * 提交广告审核
//     */
//    @ApiOperation("提交广告审核")
//    @ApiResponses({
//        @ApiResponse(code = 200, message = "提交审核成功"),
//        @ApiResponse(code = 404, message = "广告不存在")
//    })
//    @PostMapping("/{id}/submit")
//    @PreAuthorize("hasRole('ADVERTISER')")
//    public AdvertisementDTO submit(@PathVariable Long id) {
//        return advertisementService.submit(id);
//    }
//
//    /**
//     * 审核通过
//     */
//    @ApiOperation("审核通过")
//    @ApiResponses({
//        @ApiResponse(code = 200, message = "审核通过成功"),
//        @ApiResponse(code = 404, message = "广告不存在")
//    })
//    @PostMapping("/{id}/approve")
//    @PreAuthorize("hasRole('ADMIN')")
//    public AdvertisementDTO approve(@PathVariable Long id) {
//        return advertisementService.approve(id);
//    }
//
//    /**
//     * 审核拒绝
//     */
//    @ApiOperation("审核拒绝")
//    @ApiResponses({
//        @ApiResponse(code = 200, message = "审核拒绝成功"),
//        @ApiResponse(code = 404, message = "广告不存在")
//    })
//    @PostMapping("/{id}/reject")
//    @PreAuthorize("hasRole('ADMIN')")
//    public AdvertisementDTO reject(@PathVariable Long id, @RequestParam String reason) {
//        return advertisementService.reject(id, reason);
//    }
//
//    /**
//     * 开始投放
//     */
//    @ApiOperation("开始投放")
//    @ApiResponses({
//        @ApiResponse(code = 200, message = "开始投放成功"),
//        @ApiResponse(code = 404, message = "广告不存在")
//    })
//    @PostMapping("/{id}/start")
//    @PreAuthorize("hasRole('ADVERTISER')")
//    public AdvertisementDTO start(@PathVariable Long id) {
//        return advertisementService.start(id);
//    }
//
//    /**
//     * 暂停投放
//     */
//    @ApiOperation("暂停投放")
//    @ApiResponses({
//        @ApiResponse(code = 200, message = "暂停投放成功"),
//        @ApiResponse(code = 404, message = "广告不存在")
//    })
//    @PostMapping("/{id}/pause")
//    @PreAuthorize("hasRole('ADVERTISER')")
//    public AdvertisementDTO pause(@PathVariable Long id) {
//        return advertisementService.pause(id);
//    }
//
//    /**
//     * 删除广告
//     */
//    @ApiOperation("删除广告")
//    @ApiResponses({
//        @ApiResponse(code = 200, message = "删除成功"),
//        @ApiResponse(code = 404, message = "广告不存在")
//    })
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADVERTISER')")
//    public void delete(@PathVariable Long id) {
//        advertisementService.delete(id);
//    }
//}