package com.adplatform.module.ad.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 广告素材数据传输对象
 *
 * @author andrew
 * @date 2023-12-19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialDTO {
    /** 
     * 
     * 素材ID 
     */
    private Long id;

    /** 
     * 
     * 关联的广告ID 
     */
    private Long adId;

    /** 
     * 
     * 素材类型（数字编码）
     */
    private Integer type;

    /** 
     * 
     * 素材类型名称 
     */
    private String typeName;
    
    /** 
     *  
     * 素材内容
     */
    private String content;
    
    /** 
     * 
     * 素材URL地址 
     */
    private String url;
    
    /** 
     * 
     * 素材文件大小（字节） 
     */
    private Long size;
    
    /** 
     * 
     * 创建时间 
     */
    private LocalDateTime createTime;
} 