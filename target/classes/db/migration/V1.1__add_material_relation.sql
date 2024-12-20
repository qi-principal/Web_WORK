-- 1. 备份现有数据
CREATE TABLE `ad_material_backup` AS SELECT * FROM `ad_material`;

-- 2. 创建广告素材关联表
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

-- 3. 迁移现有关联关系到新表
INSERT INTO `ad_material_relation` (`ad_id`, `material_id`, `create_time`)
SELECT `ad_id`, `id`, `create_time`
FROM `ad_material`
WHERE `ad_id` IS NOT NULL;

-- 4. 修改素材表结构
ALTER TABLE `ad_material` 
    DROP FOREIGN KEY IF EXISTS `fk_material_ad`,
    DROP KEY IF EXISTS `idx_ad_id`,
    DROP COLUMN `ad_id`;

-- 5. 回滚脚本（如果需要回滚，请取消注释以下内容）
/*
-- 删除关联表
DROP TABLE IF EXISTS `ad_material_relation`;

-- 恢复素材表结构
ALTER TABLE `ad_material`
    ADD COLUMN `ad_id` bigint COMMENT '广告ID',
    ADD KEY `idx_ad_id` (`ad_id`),
    ADD CONSTRAINT `fk_material_ad` FOREIGN KEY (`ad_id`) REFERENCES `advertisement` (`id`);

-- 恢复数据
UPDATE `ad_material` m
INNER JOIN `ad_material_backup` b ON m.id = b.id
SET m.ad_id = b.ad_id;

-- 删除备份表
DROP TABLE IF EXISTS `ad_material_backup`;
*/ 