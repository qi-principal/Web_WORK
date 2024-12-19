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
    date = "2024-12-19T21:11:15+0800",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 11.0.24 (Amazon.com Inc.)"
)
@Component
public class AdConverterImpl implements AdConverter {

    @Override
    public AdvertisementDTO toDTO(Advertisement entity) {
        if ( entity == null ) {
            return null;
        }

        AdvertisementDTO advertisementDTO = new AdvertisementDTO();

        advertisementDTO.setTypeName( getTypeName( entity.getType() ) );
        advertisementDTO.setStatusName( getStatusName( entity.getStatus() ) );
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
        advertisementDTO.setCreateTime( entity.getCreateTime() );
        advertisementDTO.setUpdateTime( entity.getUpdateTime() );
        advertisementDTO.setMaterials( materialListToMaterialDTOList( entity.getMaterials() ) );

        return advertisementDTO;
    }

    @Override
    public MaterialDTO toDTO(Material entity) {
        if ( entity == null ) {
            return null;
        }

        MaterialDTO materialDTO = new MaterialDTO();

        materialDTO.setTypeName( getMaterialTypeName( entity.getType() ) );
        materialDTO.setId( entity.getId() );
        materialDTO.setAdId( entity.getAdId() );
        materialDTO.setType( entity.getType() );
        materialDTO.setContent( entity.getContent() );
        materialDTO.setUrl( entity.getUrl() );
        materialDTO.setSize( entity.getSize() );
        materialDTO.setCreateTime( entity.getCreateTime() );

        return materialDTO;
    }

    protected List<MaterialDTO> materialListToMaterialDTOList(List<Material> list) {
        if ( list == null ) {
            return null;
        }

        List<MaterialDTO> list1 = new ArrayList<MaterialDTO>( list.size() );
        for ( Material material : list ) {
            list1.add( toDTO( material ) );
        }

        return list1;
    }
}
