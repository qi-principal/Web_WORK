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
    date = "2024-12-24T02:24:44+0800",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.12 (Amazon.com Inc.)"
)
@Component
public class AdvertisementConverterImpl implements AdvertisementConverter {

    @Override
    public Advertisement toEntity(AdvertisementDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Advertisement advertisement = new Advertisement();

        advertisement.setTitle( dto.getTitle() );
        advertisement.setDescription( dto.getDescription() );
        advertisement.setUserId( dto.getUserId() );
        advertisement.setType( dto.getType() );
        advertisement.setStatus( dto.getStatus() );
        advertisement.setBudget( dto.getBudget() );
        advertisement.setDailyBudget( dto.getDailyBudget() );
        advertisement.setStartTime( dto.getStartTime() );
        advertisement.setEndTime( dto.getEndTime() );
        advertisement.setClickUrl( dto.getClickUrl() );
        advertisement.setMaterials( materialDTOListToMaterialList( dto.getMaterials() ) );

        return advertisement;
    }

    @Override
    public AdvertisementDTO toDTO(Advertisement entity) {
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

        advertisementDTO.setTypeName( getTypeName(entity.getType()) );
        advertisementDTO.setStatusName( getStatusName(entity.getStatus()) );

        return advertisementDTO;
    }

    @Override
    public void updateEntity(Advertisement entity, AdvertisementDTO dto) {
        if ( dto == null ) {
            return;
        }

        entity.setTitle( dto.getTitle() );
        entity.setDescription( dto.getDescription() );
        entity.setUserId( dto.getUserId() );
        entity.setType( dto.getType() );
        entity.setStatus( dto.getStatus() );
        entity.setBudget( dto.getBudget() );
        entity.setDailyBudget( dto.getDailyBudget() );
        entity.setStartTime( dto.getStartTime() );
        entity.setEndTime( dto.getEndTime() );
        entity.setClickUrl( dto.getClickUrl() );
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
    }

    protected Material materialDTOToMaterial(MaterialDTO materialDTO) {
        if ( materialDTO == null ) {
            return null;
        }

        Material material = new Material();

        material.setId( materialDTO.getId() );
        material.setType( materialDTO.getType() );
        material.setContent( materialDTO.getContent() );
        material.setUrl( materialDTO.getUrl() );
        material.setSize( materialDTO.getSize() );
        material.setCreateTime( materialDTO.getCreateTime() );

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

        materialDTO.setId( material.getId() );
        materialDTO.setType( material.getType() );
        materialDTO.setContent( material.getContent() );
        materialDTO.setUrl( material.getUrl() );
        materialDTO.setSize( material.getSize() );
        materialDTO.setCreateTime( material.getCreateTime() );

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
