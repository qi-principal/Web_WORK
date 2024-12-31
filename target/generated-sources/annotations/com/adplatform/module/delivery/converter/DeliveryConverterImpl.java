package com.adplatform.module.delivery.converter;

import com.adplatform.module.delivery.dto.request.DeliveryTaskRequest;
import com.adplatform.module.delivery.dto.response.DeliveryTaskResponse;
import com.adplatform.module.delivery.entity.AdDeliveryTask;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-27T20:56:56+0800",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.9 (Amazon.com Inc.)"
)
public class DeliveryConverterImpl implements DeliveryConverter {

    @Override
    public AdDeliveryTask toEntity(DeliveryTaskRequest request) {
        if ( request == null ) {
            return null;
        }

        AdDeliveryTask adDeliveryTask = new AdDeliveryTask();

        adDeliveryTask.setAdId( request.getAdId() );
        adDeliveryTask.setAdSpaceId( request.getAdSpaceId() );
        adDeliveryTask.setStartTime( request.getStartTime() );
        adDeliveryTask.setEndTime( request.getEndTime() );
        adDeliveryTask.setPriority( request.getPriority() );

        adDeliveryTask.setStatus( 0 );

        return adDeliveryTask;
    }

    @Override
    public DeliveryTaskResponse toResponse(AdDeliveryTask entity) {
        if ( entity == null ) {
            return null;
        }

        DeliveryTaskResponse deliveryTaskResponse = new DeliveryTaskResponse();

        deliveryTaskResponse.setStartTime( entity.getStartTime() );
        deliveryTaskResponse.setEndTime( entity.getEndTime() );
        deliveryTaskResponse.setId( entity.getId() );
        deliveryTaskResponse.setAdId( entity.getAdId() );
        deliveryTaskResponse.setAdSpaceId( entity.getAdSpaceId() );
        deliveryTaskResponse.setCreateTime( entity.getCreateTime() );
        deliveryTaskResponse.setUpdateTime( entity.getUpdateTime() );
        deliveryTaskResponse.setPriority( entity.getPriority() );

        deliveryTaskResponse.setStatus( entity.getDeliveryStatus() );

        return deliveryTaskResponse;
    }
}
