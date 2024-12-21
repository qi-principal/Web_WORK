package com.adplatform.module.delivery.converter;

import com.adplatform.module.delivery.dto.request.DeliveryTaskRequest;
import com.adplatform.module.delivery.dto.response.DeliveryTaskResponse;
import com.adplatform.module.delivery.entity.AdDeliveryTask;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-22T02:21:17+0800",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.12 (Amazon.com Inc.)"
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

        deliveryTaskResponse.setId( entity.getId() );
        deliveryTaskResponse.setAdId( entity.getAdId() );
        deliveryTaskResponse.setAdSpaceId( entity.getAdSpaceId() );
        deliveryTaskResponse.setCreateTime( entity.getCreateTime() );
        deliveryTaskResponse.setUpdateTime( entity.getUpdateTime() );

        deliveryTaskResponse.setStatus( entity.getDeliveryStatus() );

        return deliveryTaskResponse;
    }
}
