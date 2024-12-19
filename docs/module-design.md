# 广告投放系统 - 模块设计说明

## 1. 用户模块设计

### 1.1 数据库设计
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

### 1.2 核心功能
- 用户注册
- 用户登录（JWT认证）
- 用户信息管理
- 角色权限管理
- 账户余额管理

## 2. 广告模块设计

### 2.1 数据库设计
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

### 2.2 核心功能
- 广告创建与管理
- 广告素材上传
- 广告审核流程
- 广告投放控制
- 广告数据统计

## 3. 网站模块设计

### 3.1 数据库设计
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

### 3.2 核心功能
- 网站管理
- 广告位管理
- 广告代码生成
- 广告展示控制

## 4. 统计模块设计

### 4.1 数据��设计
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

### 4.2 核心功能
- 广告展示统计
- 广告点击统计
- 广告效果分析
- 数据报表生成

## 5. 支付模块设计

### 5.1 数据库设计
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

### 5.2 核心功能
- 充值功能
- 支付集成
- 交易记录
- 对账功能 