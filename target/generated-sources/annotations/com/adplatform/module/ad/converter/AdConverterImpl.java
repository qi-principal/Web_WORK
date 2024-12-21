package com.adplatform.module.ad.converter;

import com.adplatform.module.ad.dto.AdvertisementDTO;
import com.adplatform.module.ad.dto.MaterialDTO;
import com.adplatform.module.ad.entity.Advertisement;
import com.adplatform.module.ad.entity.Material;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-22T02:21:17+0800",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.12 (Amazon.com Inc.)"
)
@Component
public class AdConverterImpl implements AdConverter {

    @Override
    public AdvertisementDTO toAdvertisementDTO(Advertisement entity) {
        if ( entity == null ) {
            return null;
        }

        AdvertisementDTO advertisementDTO = new AdvertisementDTO();

        advertisementDTO.setId( entity.getId() );
        advertisementDTO.setTitle( entity.getTitle() );
        advertisementDTO.setDescription( entity.getDescription() );
        advertisementDTO.setUserId( entity.getUserId() );
        advertisementDTO.setType( entity.getType() );
        advertisementDTO.setStatus( entity.getStatus() );
        advertisementDTO.setBudget( entity.getBudget() );
        advertisementDTO.setDailyBudget( entity.getDailyBudget() );
        advertisementDTO.setStartTime( entity.getStartTime() );
        advertisementDTO.setEndTime( entity.getEndTime() );
        advertisementDTO.setClickUrl( entity.getClickUrl() );
        advertisementDTO.setCreateTime( entity.getCreateTime() );
        advertisementDTO.setUpdateTime( entity.getUpdateTime() );
        advertisementDTO.setMaterials( materialListToMaterialDTOList( entity.getMaterials() ) );

        advertisementDTO.setTypeName( com.adplatform.module.ad.enums.AdType.getNameByCode(entity.getType()) );
        advertisementDTO.setStatusName( com.adplatform.module.ad.enums.AdStatus.getNameByCode(entity.getStatus()) );

        return advertisementDTO;
    }

    @Override
    public MaterialDTO toMaterialDTO(Material entity) {
        if ( entity == null ) {
            return null;
        }

        MaterialDTO.MaterialDTOBuilder materialDTO = MaterialDTO.builder();

        materialDTO.id( entity.getId() );
        materialDTO.type( entity.getType() );
        materialDTO.content( entity.getContent() );
        materialDTO.url( entity.getUrl() );
        materialDTO.size( entity.getSize() );
        materialDTO.createTime( entity.getCreateTime() );

        materialDTO.typeName( com.adplatform.module.ad.enums.MaterialType.getNameByCode(entity.getType()) );

        return materialDTO.build();
    }

    protected List<MaterialDTO> materialListToMaterialDTOList(List<Material> list) {
        if ( list == null ) {
            return null;
        }

        List<MaterialDTO> list1 = new ArrayList<MaterialDTO>( list.size() );
        for ( Material material : list ) {
            list1.add( toMaterialDTO( material ) );
        }

        return list1;
    }
}