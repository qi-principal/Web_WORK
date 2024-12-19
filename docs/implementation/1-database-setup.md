# 数据库初始化实现文档

## 1. 创建数据库

首先需要创建数据库并设置正确的字符集：

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS ad_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 使用数据库
USE ad_platform;
```

## 2. 创建基础表

### 2.1 用户相关表
```sql
-- 用户表
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `user_type` tinyint NOT NULL COMMENT '用户类型：1-广告主 2-网站主 3-管理员',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `balance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '账户余额',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE `role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(50) NOT NULL COMMENT '角色名称',
  `code` varchar(50) NOT NULL COMMENT '角色编码',
  `description` varchar(200) DEFAULT NULL COMMENT '角色描述',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE `user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';
```

### 2.2 广告相关表
```sql
-- 广告表
CREATE TABLE `advertisement` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '广告ID',
  `title` varchar(100) NOT NULL COMMENT '广告标题',
  `description` varchar(500) DEFAULT NULL COMMENT '广告描述',
  `user_id` bigint NOT NULL COMMENT '广告主ID',
  `type` tinyint NOT NULL COMMENT '广告类型：1-图片 2-视频 3-文字',
  `status` tinyint NOT NULL COMMENT '状态：0-草稿 1-待审核 2-审核中 3-已拒绝 4-已通过 5-投放中 6-已暂停 7-已完成',
  `budget` decimal(10,2) NOT NULL COMMENT '预算金额',
  `daily_budget` decimal(10,2) NOT NULL COMMENT '日预算',
  `start_time` datetime NOT NULL COMMENT '投放开始时间',
  `end_time` datetime NOT NULL COMMENT '投放结束时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告表';

-- 广告素材表
CREATE TABLE `ad_material` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '素材ID',
  `ad_id` bigint NOT NULL COMMENT '广告ID',
  `type` tinyint NOT NULL COMMENT '素材类型：1-图片 2-视频 3-文字',
  `content` text NOT NULL COMMENT '素材内容',
  `url` varchar(500) DEFAULT NULL COMMENT '素材URL',
  `size` bigint DEFAULT NULL COMMENT '素材大小（字节）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_ad_id` (`ad_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告素材表';
```

### 2.3 网站相关表
```sql
-- 网站表
CREATE TABLE `website` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '网站ID',
  `user_id` bigint NOT NULL COMMENT '网站主ID',
  `name` varchar(100) NOT NULL COMMENT '网站名称',
  `domain` varchar(100) NOT NULL COMMENT '网站域名',
  `description` varchar(500) DEFAULT NULL COMMENT '网站描述',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-待审核 1-正常 2-已禁用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  UNIQUE KEY `uk_domain` (`domain`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网站表';

-- 广告位表
CREATE TABLE `ad_space` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '广告位ID',
  `website_id` bigint NOT NULL COMMENT '网站ID',
  `name` varchar(100) NOT NULL COMMENT '广告位名称',
  `type` tinyint NOT NULL COMMENT '广告位类型：1-横幅 2-侧边栏 3-弹窗',
  `width` int NOT NULL COMMENT '宽度',
  `height` int NOT NULL COMMENT '高度',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_website_id` (`website_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告位表';
```

### 2.4 统计相关表
```sql
-- 广告展示记录表
CREATE TABLE `ad_impression` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `ad_id` bigint NOT NULL COMMENT '广告ID',
  `ad_space_id` bigint NOT NULL COMMENT '广告位ID',
  `ip` varchar(50) NOT NULL COMMENT '访问IP',
  `user_agent` varchar(500) NOT NULL COMMENT '用户代理',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_ad_id` (`ad_id`),
  KEY `idx_ad_space_id` (`ad_space_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告展示记录表';

-- 广告点击记录表
CREATE TABLE `ad_click` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `ad_id` bigint NOT NULL COMMENT '广告ID',
  `ad_space_id` bigint NOT NULL COMMENT '广告位ID',
  `ip` varchar(50) NOT NULL COMMENT '访问IP',
  `user_agent` varchar(500) NOT NULL COMMENT '用户代理',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_ad_id` (`ad_id`),
  KEY `idx_ad_space_id` (`ad_space_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告点击记录表';

-- 数据统计表（按天）
CREATE TABLE `ad_stats_daily` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `ad_id` bigint NOT NULL COMMENT '广告ID',
  `date` date NOT NULL COMMENT '统计日期',
  `impressions` int NOT NULL DEFAULT '0' COMMENT '展示次数',
  `clicks` int NOT NULL DEFAULT '0' COMMENT '点击次数',
  `cost` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '消费金额',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ad_date` (`ad_id`,`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告统计表（按天）';
```

### 2.5 支付相关表
```sql
-- 充值订单表
CREATE TABLE `recharge_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `amount` decimal(10,2) NOT NULL COMMENT '充值金额',
  `pay_type` tinyint NOT NULL COMMENT '支付方式：1-支付宝 2-微信',
  `status` tinyint NOT NULL COMMENT '状态：0-待支付 1-支付中 2-支付成功 3-支付失败',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充值订单表';

-- 交易记录表
CREATE TABLE `transaction` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '交易ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `type` tinyint NOT NULL COMMENT '交易类型：1-充值 2-消费 3-退款',
  `amount` decimal(10,2) NOT NULL COMMENT '交易金额',
  `balance` decimal(10,2) NOT NULL COMMENT '交易后余额',
  `description` varchar(200) DEFAULT NULL COMMENT '交易描述',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易记录表';
```

## 3. 初始化基础数据

### 3.1 初始化角色数据
```sql
-- 插入基础角色
INSERT INTO `role` (`code`, `name`, `description`, `create_time`, `update_time`) VALUES
('ROLE_ADMIN', '管理员', '系统管理员', NOW(), NOW()),
('ROLE_ADVERTISER', '广告主', '广告投放者', NOW(), NOW()),
('ROLE_PUBLISHER', '网站主', '广告展示者', NOW(), NOW());
```

### 3.2 初始化管理员账号
```sql
-- 插入管理员账号（密码为加密后的 admin123）
INSERT INTO `user` 
(`username`, `password`, `email`, `user_type`, `status`, `create_time`, `update_time`) 
VALUES 
('admin', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 
'admin@example.com', 3, 1, NOW(), NOW());

-- 关联管理员角色
INSERT INTO `user_role` (`user_id`, `role_id`) 
SELECT u.id, r.id 
FROM `user` u, `role` r 
WHERE u.username = 'admin' AND r.code = 'ROLE_ADMIN';
```

## 4. 数据库维护建议

### 4.1 备份策略
```bash
# 创建备份目录
mkdir -p /backup/database

# 设置定时备份任务
# 编辑crontab
crontab -e

# 添加定时任务（每天凌晨3点执行）
0 3 * * * mysqldump -u root -p'your_password' ad_platform > /backup/database/ad_platform_$(date +\%Y\%m\%d).sql
```

### 4.2 性能优化
```sql
-- 定期更新统计信息
ANALYZE TABLE user, advertisement, website, ad_impression, ad_click;

-- 定期优化表
OPTIMIZE TABLE ad_impression, ad_click;

-- 清理历史数据（保留30天）
DELETE FROM ad_impression WHERE create_time < DATE_SUB(NOW(), INTERVAL 30 DAY);
DELETE FROM ad_click WHERE create_time < DATE_SUB(NOW(), INTERVAL 30 DAY);
```

### 4.3 监控建议
1. 监控数据库连接数
```sql
SHOW STATUS LIKE 'Threads_connected';
```

2. 监控慢查询
```sql
SHOW VARIABLES LIKE '%slow_query%';
SHOW VARIABLES LIKE '%long_query_time%';
```

3. 监控表空间
```sql
SELECT 
    table_name AS '表名',
    table_rows AS '行数',
    data_length/1024/1024 AS '数据容量(MB)',
    index_length/1024/1024 AS '索引容量(MB)'
FROM 
    information_schema.tables
WHERE 
    table_schema = 'ad_platform'
ORDER BY 
    data_length DESC;