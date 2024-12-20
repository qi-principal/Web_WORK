# 广告平台数据库设计文档

## 1. 数据库概述

本文档详细说明广告平台的数据库设计，包括表结构、字段说明、索引设计等内容。数据库采用 MySQL 8.0，字符集使用 utf8mb4。

## 2. 表结构说明

### 2.1 广告表 (advertisement)

广告主表，存储广告的基本信息。

```sql
CREATE TABLE `advertisement` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '广告ID',
    `title` varchar(100) NOT NULL COMMENT '广告标题',
    `description` varchar(500) DEFAULT NULL COMMENT '广告描述',
    `user_id` bigint NOT NULL COMMENT '广告主ID',
    `type` int NOT NULL COMMENT '广告类型：1-图片广告、2-视频广告、3-文字广告',
    `status` int NOT NULL COMMENT '状态：0-草稿、1-待审核、2-审核中、3-已拒绝、4-已通过、5-投放中、6-已暂停、7-已完成',
    `budget` decimal(10,2) NOT NULL COMMENT '总预算',
    `daily_budget` decimal(10,2) NOT NULL COMMENT '日预算',
    `start_time` datetime NOT NULL COMMENT '投放开始时间',
    `end_time` datetime NOT NULL COMMENT '投放结束时间',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告表';
```

### 2.2 素材表 (ad_material)

存储广告素材信息。

```sql
CREATE TABLE `ad_material` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '素材ID',
    `type` int NOT NULL COMMENT '素材类型：1-图片、2-视频、3-文字',
    `content` varchar(500) DEFAULT NULL COMMENT '素材内容',
    `url` varchar(255) NOT NULL COMMENT '素材URL',
    `size` bigint NOT NULL COMMENT '素材大小（字节）',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告素材表';
```

### 2.3 广告素材关联表 (ad_material_relation)

管理广告与素材的多对多关系。

```sql
CREATE TABLE `ad_material_relation` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `ad_id` bigint NOT NULL COMMENT '广告ID',
    `material_id` bigint NOT NULL COMMENT '素材ID',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_ad_material` (`ad_id`, `material_id`),
    KEY `idx_ad_id` (`ad_id`),
    KEY `idx_material_id` (`material_id`),
    CONSTRAINT `fk_relation_ad` FOREIGN KEY (`ad_id`) REFERENCES `advertisement` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_relation_material` FOREIGN KEY (`material_id`) REFERENCES `ad_material` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告素材关联表';
```

### 2.4 用户表 (user)

存储用户基本信息。

```sql
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
```

### 2.5 角色表 (role)

存储系统角色信息。

```sql
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
```

### 2.6 用户角色关联表 (user_role)

管理用户与角色的多对多关系。

```sql
CREATE TABLE `user_role` (
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `role_id` bigint NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';
```

## 3. 索引设计说明

### 3.1 主键索引
- 所有表都使用自增的 `id` 字段作为主键，提供唯一标识和高效的数据访问。
- `user_role` 表使用联合主键（user_id, role_id）。

### 3.2 唯一索引
- `user` 表：用户名和邮箱字段添加唯一索引，确保唯一性。
- `role` 表：角色编码字段添加唯一索引，确保唯一性。
- `ad_material_relation` 表：广告ID和素材ID的组合添加唯一索引，防止重复关联。

### 3.3 普通索引
- 创建时间索引：用于按时间范围查询和排序。
- 状态索引：用于按状态筛选数据。
- 外键索引：用于关联查询优化。
- 用户类型索引：用于按用户类型查询。

## 4. 字段类型说明

### 4.1 数值类型
- `id`：使用 `bigint` 类型，支持大量数据。
- `user_type`/`status`：使用 `tinyint` 类型，存储枚举值。
- `balance`：使用 `decimal(10,2)` 类型，精确存储金额。
- `budget`/`daily_budget`：使用 `decimal(10,2)` 类型，精确存储金额。

### 4.2 字符串类型
- 短文本：使用 `varchar(50)` 或 `varchar(100)`。
- 长文本：使用 `varchar(500)` 或更大。
- URL：使用 `varchar(255)`。
- 角色编码：使用 `varchar(50)`，存储角色标识。

### 4.3 时间类型
- 所有时间字段使用 `datetime` 类型，精确到秒。

## 5. 约束说明

### 5.1 外键约束
- `ad_material_relation` 表中的外键采用级联删除（CASCADE），当删除广告或素材时自动删除关联记录。
- `user_role` 表中的外键采用级联删除（CASCADE），当删除用户或角色时自动删除关联记录。

### 5.2 非空约束
- 重要字段都添加了非空约束，确保数据完整性。
- 用户表的用户名、密码、邮箱、用户类型字段不允许为空。
- 角色表的 `name` 和 `code` 字段不允许为空。
- 用户角色关联表的所有字段都不允许为空。

### 5.3 默认值
- 用户表的 `status` 字段默认值为 1（启用）。
- 用户表的 `balance` 字段默认值为 0.00。
- 部分可选字段（如 `description`、`phone`）允许为 NULL。
- 角色描述字段允许为 NULL。

## 6. 注意事项

1. 所有表都使用 InnoDB 存储引擎，支持事务和外键。
2. 字符集统一使用 utf8mb4，支持完整的 Unicode 字符集。
3. 时间字段统一使用 datetime 类型，便于时区处理。
4. 重要的查询字段都添加了适当的索引，优化查询性能。
5. 使用外键约束确保数据的引用完整性。
6. 角色编码使用统一的命名规范，便于权限管理。
7. 用户角色关联采用多对多设计，支持用户拥有多个角色。
8. 用户类型和状态使用 tinyint 类型，节省存储空间。
9. 账户余额使用 decimal 类型，确保金额计算精确性。 