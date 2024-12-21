package com.adplatform.module.ad.converter;

import com.adplatform.module.ad.dto.AdvertisementDTO;
import com.adplatform.module.ad.entity.Advertisement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * 广告转换器
 */
@Mapper(componentModel = "spring")
public interface AdvertisementConverter {

    AdvertisementConverter INSTANCE = Mappers.getMapper(AdvertisementConverter.class);

    /**
     * DTO转实体
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    Advertisement toEntity(AdvertisementDTO dto);

    /**
     * 实体转DTO
     */
    @Mapping(target = "typeName", expression = "java(getTypeName(entity.getType()))")
    @Mapping(target = "statusName", expression = "java(getStatusName(entity.getStatus()))")
    AdvertisementDTO toDTO(Advertisement entity);

    /**
     * 更新实体
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    void updateEntity(@MappingTarget Advertisement entity, AdvertisementDTO dto);

    /**
     * 获取广告类型名称
     */
    default String getTypeName(Integer type) {
        if (type == null) return "";
        switch (type) {
            case 1:
                return "图片";
            case 2:
                return "视频";
            case 3:
                return "文字";
            default:
                return "未知";
        }
    }

    /**
     * 获取广告状态名称
     */
    default String getStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0:
                return "草稿";
            case 1:
                return "待审核";
            case 2:
                return "审核中";
            case 3:
                return "已拒绝";
            case 4:
                return "已通过";
            case 5:
                return "投放中";
            case 6:
                return "已暂停";
            case 7:
                return "已完成";
            default:
                return "未知";
        }
    }
} 