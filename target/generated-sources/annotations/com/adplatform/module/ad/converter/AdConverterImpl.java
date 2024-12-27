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
    date = "2024-12-29T20:25:00+0800",
    comments = "version: 1.5.3.Final, compiler: Eclipse JDT (IDE) 3.41.0.v20241217-1506, environment: Java 17.0.13 (Eclipse Adoptium)"
)
@Component
public class AdConverterImpl implements AdConverter {

    @Override
    public AdvertisementDTO toAdvertisementDTO(Advertisement entity) {
        if ( entity == null ) {
            return null;
        }

        AdvertisementDTO advertisementDTO = new AdvertisementDTO();

        advertisementDTO.setBudget( entity.getBudget() );
        advertisementDTO.setClickUrl( entity.getClickUrl() );
        advertisementDTO.setCreateTime( entity.getCreateTime() );
        advertisementDTO.setDailyBudget( entity.getDailyBudget() );
        advertisementDTO.setDescription( entity.getDescription() );
        advertisementDTO.setEndTime( entity.getEndTime() );
        advertisementDTO.setId( entity.getId() );
        advertisementDTO.setMaterials( materialListToMaterialDTOList( entity.getMaterials() ) );
        advertisementDTO.setStartTime( entity.getStartTime() );
        advertisementDTO.setStatus( entity.getStatus() );
        advertisementDTO.setTitle( entity.getTitle() );
        advertisementDTO.setType( entity.getType() );
        advertisementDTO.setUpdateTime( entity.getUpdateTime() );
        advertisementDTO.setUserId( entity.getUserId() );

        advertisementDTO.setTypeName( com.adplatform.module.ad.enums.AdType.getNameByCode(entity.getType()) );
        advertisementDTO.setStatusName( com.adplatform.module.ad.enums.AdStatus.getNameByCode(entity.getStatus()) );

        return advertisementDTO;
    }

    @Override
    public MaterialDTO toMaterialDTO(Material entity) {
        if ( entity == null ) {
            return null;
        }

        MaterialDTO materialDTO = new MaterialDTO();

        materialDTO.setContent( entity.getContent() );
        materialDTO.setCreateTime( entity.getCreateTime() );
        materialDTO.setId( entity.getId() );
        materialDTO.setSize( entity.getSize() );
        materialDTO.setType( entity.getType() );
        materialDTO.setUrl( entity.getUrl() );

        materialDTO.setTypeName( com.adplatform.module.ad.enums.MaterialType.getNameByCode(entity.getType()) );

        return materialDTO;
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
