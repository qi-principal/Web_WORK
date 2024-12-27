package com.adplatform.module.delivery.converter;

import com.adplatform.module.delivery.dto.request.DeliveryTaskRequest;
import com.adplatform.module.delivery.dto.response.DeliveryTaskResponse;
import com.adplatform.module.delivery.entity.AdDeliveryTask;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-29T20:25:00+0800",
    comments = "version: 1.5.3.Final, compiler: Eclipse JDT (IDE) 3.41.0.v20241217-1506, environment: Java 17.0.13 (Eclipse Adoptium)"
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
        adDeliveryTask.setEndTime( request.getEndTime() );
        adDeliveryTask.setPriority( request.getPriority() );
        adDeliveryTask.setStartTime( request.getStartTime() );

        adDeliveryTask.setStatus( 0 );

        return adDeliveryTask;
    }

    @Override
    public DeliveryTaskResponse toResponse(AdDeliveryTask entity) {
        if ( entity == null ) {
            return null;
        }

        DeliveryTaskResponse deliveryTaskResponse = new DeliveryTaskResponse();

        deliveryTaskResponse.setAdId( entity.getAdId() );
        deliveryTaskResponse.setAdSpaceId( entity.getAdSpaceId() );
        deliveryTaskResponse.setCreateTime( entity.getCreateTime() );
        deliveryTaskResponse.setEndTime( entity.getEndTime() );
        deliveryTaskResponse.setId( entity.getId() );
        deliveryTaskResponse.setPriority( entity.getPriority() );
        deliveryTaskResponse.setStartTime( entity.getStartTime() );
        deliveryTaskResponse.setUpdateTime( entity.getUpdateTime() );

        deliveryTaskResponse.setStatus( entity.getDeliveryStatus() );

        return deliveryTaskResponse;
    }
}
