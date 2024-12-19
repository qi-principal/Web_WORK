package com.adplatform.module.ad.converter;

import com.adplatform.module.ad.dto.AdvertisementDTO;
import com.adplatform.module.ad.dto.MaterialDTO;
import com.adplatform.module.ad.entity.Advertisement;
import com.adplatform.module.ad.entity.Material;
import com.adplatform.module.ad.enums.AdStatus;
import com.adplatform.module.ad.enums.AdType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * 广告对象转换器
 *
 * @author andrew
 * @date 2023-12-19
 */
@Mapper(componentModel = "spring")
public interface AdConverter {

    /**
     * 广告实体转DTO
     */
    @Mapping(target = "typeName", source = "type", qualifiedByName = "getTypeName")
    @Mapping(target = "statusName", source = "status", qualifiedByName = "getStatusName")
    AdvertisementDTO toDTO(Advertisement entity);

    /**
     * 广告素材实体转DTO
     */
    @Mapping(target = "typeName", source = "type", qualifiedByName = "getMaterialTypeName")
    MaterialDTO toDTO(Material entity);

    /**
     * 获取广告类型名称
     */
    @Named("getTypeName")
    default String getTypeName(Integer type) {
        AdType adType = AdType.getByCode(type);
        return adType != null ? adType.getDescription() : null;
    }

    /**
     * 获取广告状态名称
     */
    @Named("getStatusName")
    default String getStatusName(Integer status) {
        AdStatus adStatus = AdStatus.getByCode(status);
        return adStatus != null ? adStatus.getDescription() : null;
    }

    /**
     * 获取素材类型名称
     */
    @Named("getMaterialTypeName")
    default String getMaterialTypeName(Integer type) {
        AdType adType = AdType.getByCode(type);
        return adType != null ? adType.getDescription() : null;
    }
} 