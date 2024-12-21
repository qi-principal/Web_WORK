-- 创建数据库
CREATE DATABASE IF NOT EXISTS ad_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE ad_platform;

-- 网站表
CREATE TABLE IF NOT EXISTS `website` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '网站ID',
    `user_id` bigint NOT NULL COMMENT '所属用户ID',
    `name` varchar(100) NOT NULL COMMENT '网站名称',
    `url` varchar(255) NOT NULL COMMENT '网站URL',
    `description` varchar(500) DEFAULT NULL COMMENT '网站描述',
    `status` int NOT NULL DEFAULT '0' COMMENT '状态：0-待审核、1-已审核、2-已拒绝',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='网站表';

-- 广告位表
CREATE TABLE IF NOT EXISTS `ad_space` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '广告位ID',
    `website_id` bigint NOT NULL COMMENT '所属网站ID',
    `name` varchar(100) NOT NULL COMMENT '广告位名称',
    `width` int NOT NULL COMMENT '广告位宽度',
    `height` int NOT NULL COMMENT '广告位高度',
    `code` varchar(500) NOT NULL COMMENT '广告位代码（iframe src）',
    `status` int NOT NULL DEFAULT '0' COMMENT '状态：0-待审核、1-已审核、2-已拒绝',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_website_name` (`website_id`, `name`),
    KEY `idx_website_id` (`website_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_ad_space_website` FOREIGN KEY (`website_id`) REFERENCES `website` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='广告位表';

-- 页面表
CREATE TABLE IF NOT EXISTS `page` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '页面ID',
    `ad_space_id` bigint NOT NULL COMMENT '广告位ID',
    `url` varchar(255) NOT NULL COMMENT '页面URL',
    `content` text DEFAULT NULL COMMENT '页面内容',
    `status` int NOT NULL DEFAULT '0' COMMENT '状态：0-待审核、1-已审核、2-已拒绝',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_ad_space_id` (`ad_space_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_page_ad_space` FOREIGN KEY (`ad_space_id`) REFERENCES `ad_space` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='页面表';

-- 添加一些基础的约束条件
ALTER TABLE `website` ADD CONSTRAINT `chk_website_status` 
    CHECK (`status` IN (0, 1, 2));

ALTER TABLE `ad_space` ADD CONSTRAINT `chk_ad_space_status` 
    CHECK (`status` IN (0, 1, 2));

ALTER TABLE `page` ADD CONSTRAINT `chk_page_status` 
    CHECK (`status` IN (0, 1, 2));

ALTER TABLE `ad_space` ADD CONSTRAINT `chk_ad_space_dimensions` 
    CHECK (`width` > 0 AND `height` > 0);

-- 添加一些注释
COMMENT ON TABLE `website` IS '网站信息表';
COMMENT ON TABLE `ad_space` IS '广告位信息表';
COMMENT ON TABLE `page` IS '页面信息表'; 