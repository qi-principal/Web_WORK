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
public class AdvertisementConverterImpl implements AdvertisementConverter {

    @Override
    public Advertisement toEntity(AdvertisementDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Advertisement advertisement = new Advertisement();

        advertisement.setBudget( dto.getBudget() );
        advertisement.setClickUrl( dto.getClickUrl() );
        advertisement.setDailyBudget( dto.getDailyBudget() );
        advertisement.setDescription( dto.getDescription() );
        advertisement.setEndTime( dto.getEndTime() );
        advertisement.setMaterials( materialDTOListToMaterialList( dto.getMaterials() ) );
        advertisement.setStartTime( dto.getStartTime() );
        advertisement.setStatus( dto.getStatus() );
        advertisement.setTitle( dto.getTitle() );
        advertisement.setType( dto.getType() );
        advertisement.setUserId( dto.getUserId() );

        return advertisement;
    }

    @Override
    public AdvertisementDTO toDTO(Advertisement entity) {
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

        advertisementDTO.setTypeName( getTypeName(entity.getType()) );
        advertisementDTO.setStatusName( getStatusName(entity.getStatus()) );

        return advertisementDTO;
    }

    @Override
    public void updateEntity(Advertisement entity, AdvertisementDTO dto) {
        if ( dto == null ) {
            return;
        }

        entity.setBudget( dto.getBudget() );
        entity.setClickUrl( dto.getClickUrl() );
        entity.setDailyBudget( dto.getDailyBudget() );
        entity.setDescription( dto.getDescription() );
        entity.setEndTime( dto.getEndTime() );
        if ( entity.getMaterials() != null ) {
            List<Material> list = materialDTOListToMaterialList( dto.getMaterials() );
            if ( list != null ) {
                entity.getMaterials().clear();
                entity.getMaterials().addAll( list );
            }
            else {
                entity.setMaterials( null );
            }
        }
        else {
            List<Material> list = materialDTOListToMaterialList( dto.getMaterials() );
            if ( list != null ) {
                entity.setMaterials( list );
            }
        }
        entity.setStartTime( dto.getStartTime() );
        entity.setStatus( dto.getStatus() );
        entity.setTitle( dto.getTitle() );
        entity.setType( dto.getType() );
        entity.setUserId( dto.getUserId() );
    }

    protected Material materialDTOToMaterial(MaterialDTO materialDTO) {
        if ( materialDTO == null ) {
            return null;
        }

        Material material = new Material();

        material.setContent( materialDTO.getContent() );
        material.setCreateTime( materialDTO.getCreateTime() );
        material.setId( materialDTO.getId() );
        material.setSize( materialDTO.getSize() );
        material.setType( materialDTO.getType() );
        material.setUrl( materialDTO.getUrl() );

        return material;
    }

    protected List<Material> materialDTOListToMaterialList(List<MaterialDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<Material> list1 = new ArrayList<Material>( list.size() );
        for ( MaterialDTO materialDTO : list ) {
            list1.add( materialDTOToMaterial( materialDTO ) );
        }

        return list1;
    }

    protected MaterialDTO materialToMaterialDTO(Material material) {
        if ( material == null ) {
            return null;
        }

        MaterialDTO materialDTO = new MaterialDTO();

        materialDTO.setContent( material.getContent() );
        materialDTO.setCreateTime( material.getCreateTime() );
        materialDTO.setId( material.getId() );
        materialDTO.setSize( material.getSize() );
        materialDTO.setType( material.getType() );
        materialDTO.setUrl( material.getUrl() );

        return materialDTO;
    }

    protected List<MaterialDTO> materialListToMaterialDTOList(List<Material> list) {
        if ( list == null ) {
            return null;
        }

        List<MaterialDTO> list1 = new ArrayList<MaterialDTO>( list.size() );
        for ( Material material : list ) {
            list1.add( materialToMaterialDTO( material ) );
        }

        return list1;
    }
}
