package com.adplatform.module.delivery.service;

/**
 * CDN服务接口
 */
public interface CdnService {
    
    /**
     * 上传文件到CDN
     * @param content 文件内容
     * @param fileName 文件名
     * @return CDN访问URL
     */
    String uploadFile(String content, String fileName);
    
    /**
     * 从CDN删除文件
     * @param fileName 文件名
     */
    void deleteFile(String fileName);
    
    /**
     * 生成CDN访问URL
     * @param fileName 文件名
     * @return CDN访问URL
     */
    String generateUrl(String fileName);
} 