package com.adplatform.module.delivery.converter;

import com.adplatform.module.delivery.dto.request.DeliveryTaskRequest;
import com.adplatform.module.delivery.dto.response.DeliveryTaskResponse;
import com.adplatform.module.delivery.entity.AdDeliveryTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 广告投放对象转换器
 */
@Mapper
public interface DeliveryConverter {
    
    DeliveryConverter INSTANCE = Mappers.getMapper(DeliveryConverter.class);

    /**
     * 将请求DTO转换为实体
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "0")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    AdDeliveryTask toEntity(DeliveryTaskRequest request);

    /**
     * 将实体转换为响应DTO
     */
    @Mapping(target = "status", expression = "java(entity.getDeliveryStatus())")
    DeliveryTaskResponse toResponse(AdDeliveryTask entity);
} 