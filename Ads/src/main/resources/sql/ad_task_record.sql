CREATE TABLE IF NOT EXISTS `ad_task_record` (
    `record_id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    `task_id` INT NOT NULL COMMENT '任务ID',
    `record_type` VARCHAR(20) NOT NULL COMMENT '记录类型：DISPLAY-展示，CLICK-点击',
    `record_time` DATETIME NOT NULL COMMENT '记录时间',
    `client_ip` VARCHAR(50) COMMENT '客户端IP',
    `user_agent` VARCHAR(500) COMMENT '用户代理',
    INDEX `idx_task_id` (`task_id`),
    INDEX `idx_record_type` (`record_type`),
    INDEX `idx_record_time` (`record_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告任务记录表';