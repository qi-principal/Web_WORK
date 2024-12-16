-- 创建广告任务表
CREATE TABLE IF NOT EXISTS `ad_task` (
    `task_id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '任务ID',
    `website_id` INT NOT NULL COMMENT '目标网站ID',
    `ad_slot_id` VARCHAR(255) NOT NULL COMMENT '广告位信息',
    `task_status` VARCHAR(50) NOT NULL COMMENT '任务状态：PENDING-待处理，RUNNING-进行中，COMPLETED-已完成，FAILED-失败',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_website_id` (`website_id`),
    INDEX `idx_task_status` (`task_status`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告任务表';
