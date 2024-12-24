package com.adplatform.module.ad.mapper;

import com.adplatform.module.ad.entity.AdMaterialRelation;
import com.adplatform.module.ad.entity.Material;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 广告素材关联数据访问接口
 *
 * @author andrew
 * @date 2023-12-19
 */
@Mapper
public interface AdMaterialRelationMapper extends BaseMapper<AdMaterialRelation> {
    
    /**
     * 根据广告ID查询素材列表
     *
     * @param adId 广告ID
     * @return 素材列表
     */
    @Select("SELECT m.* FROM ad_material m " +
            "INNER JOIN ad_material_relation r ON m.id = r.material_id " +
            "WHERE r.ad_id = #{adId}")
    List<Material> selectMaterialsByAdId(Long adId);
    
    /**
     * 根据素材ID查询广告ID列表
     *
     * @param materialId 素材ID
     * @return 广告ID列表
     */
    @Select("SELECT ad_id FROM ad_material_relation WHERE material_id = #{materialId}")
    List<Long> selectAdIdsByMaterialId(Long materialId);

    /**
     * 根据用户ID查询素材列表（带分页）
     *
     * @param page 分页参数
     * @param userId 用户ID
     * @return 分页后的素材列表
     */
    @Select("SELECT DISTINCT m.* FROM ad_material m " +
            "INNER JOIN ad_material_relation r ON m.id = r.material_id " +
            "INNER JOIN advertisement a ON r.ad_id = a.id " +
            "WHERE a.user_id = #{userId}")
    IPage<Material> selectMaterialsByUserId(Page<Material> page, Long userId);

    /**
     * 根据用户ID查询素材列表（不分页）
     *
     * @param userId 用户ID
     * @return 素材列表
     */
    @Select("SELECT DISTINCT m.* FROM ad_material m " +
            "INNER JOIN ad_material_relation r ON m.id = r.material_id " +
            "INNER JOIN advertisement a ON r.ad_id = a.id " +
            "WHERE a.user_id = #{userId}")
    List<Material> selectAllMaterialsByUserId(Long userId);
} 