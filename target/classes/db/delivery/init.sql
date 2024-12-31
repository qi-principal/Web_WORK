-- 创建广告位表
CREATE TABLE IF NOT EXISTS `ad_space` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '广告位ID',
    `publisher_id` bigint NOT NULL COMMENT '网站主ID',
    `name` varchar(100) NOT NULL COMMENT '广告位名称',
    `width` int NOT NULL COMMENT '宽度',
    `height` int NOT NULL COMMENT '高度',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用、1-启用',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_publisher_id` (`publisher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告位表';

-- 创建广告展示页面表
CREATE TABLE IF NOT EXISTS `ad_display_page` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '页面ID',
    `ad_space_id` bigint NOT NULL COMMENT '广告位ID',
    `unique_path` varchar(255) NOT NULL COMMENT '唯一路径',
    `url` varchar(255) NOT NULL COMMENT '广告展示页面URL',
    `current_ad_id` bigint DEFAULT NULL COMMENT '当前展示的广告ID',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用、1-启用',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_unique_path` (`unique_path`),
    KEY `idx_ad_space_id` (`ad_space_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告展示页面表';

-- 创建广告投放任务表
CREATE TABLE IF NOT EXISTS `ad_delivery_task` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    `ad_id` bigint NOT NULL COMMENT '广告ID',
    `ad_space_id` bigint NOT NULL COMMENT 'websiteID',
    `start_time` datetime NOT NULL COMMENT '开始时间',
    `end_time` datetime NOT NULL COMMENT '结束时间',
    `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0-待投放、1-投放中、2-已完成、3-已暂停',
    `priority` int NOT NULL DEFAULT '0' COMMENT '优先级：数字越大优先级越高',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_ad_id` (`ad_id`),
    KEY `idx_ad_space_id` (`ad_space_id`),
    KEY `idx_status_priority` (`status`, `priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告投放任务表';

-- 创建广告投放作业表
CREATE TABLE IF NOT EXISTS `ad_delivery_job` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '作业ID',
    `task_id` bigint NOT NULL COMMENT '任务ID',
    `display_page_id` bigint NOT NULL COMMENT '展示页面ID',
    `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0-待执行、1-执行中、2-已完成、3-失败',
    `result` varchar(500) DEFAULT NULL COMMENT '执行结果',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_display_page_id` (`display_page_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告投放作业表'; 