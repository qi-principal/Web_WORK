package com.adplatform.module.ad.converter;

import com.adplatform.module.ad.dto.AdvertisementDTO;
import com.adplatform.module.ad.dto.MaterialDTO;
import com.adplatform.module.ad.entity.Advertisement;
import com.adplatform.module.ad.entity.Material;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 广告相关实体转换器
 *
 * @author andrew
 * @date 2023-12-19
 */
@Mapper(componentModel = "spring")
public interface AdConverter {
    
    /**
     * 将广告实体转换为DTO
     */
    @Mapping(target = "typeName", expression = "java(com.adplatform.module.ad.enums.AdType.getNameByCode(entity.getType()))")
    @Mapping(target = "statusName", expression = "java(com.adplatform.module.ad.enums.AdStatus.getNameByCode(entity.getStatus()))")
    AdvertisementDTO toAdvertisementDTO(Advertisement entity);
    
    /**
     * 将素材实体转换为DTO
     */
    @Mapping(target = "typeName", expression = "java(com.adplatform.module.ad.enums.MaterialType.getNameByCode(entity.getType()))")
    MaterialDTO toMaterialDTO(Material entity);
} 