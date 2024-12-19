-- 定期更新统计信息
ANALYZE TABLE user, advertisement, website, ad_impression, ad_click;

-- 定期优化表
OPTIMIZE TABLE ad_impression, ad_click;

-- 清理历史数据（保留30天）
DELETE FROM ad_impression WHERE create_time < DATE_SUB(NOW(), INTERVAL 30 DAY);
DELETE FROM ad_click WHERE create_time < DATE_SUB(NOW(), INTERVAL 30 DAY);

-- 监控数据库连接数
SHOW STATUS LIKE 'Threads_connected';

-- 监控慢查询
SHOW VARIABLES LIKE '%slow_query%';
SHOW VARIABLES LIKE '%long_query_time%';

-- 监控表空间（方式1：使用SHOW TABLE STATUS）
SHOW TABLE STATUS FROM ad_platform;

-- 监控表空间（方式2：使用information_schema，如果支持的话）
SELECT 
    TABLE_NAME as '表名',
    TABLE_ROWS as '行数',
    (DATA_LENGTH + INDEX_LENGTH) / 1024 / 1024 as '总大小(MB)',
    DATA_LENGTH / 1024 / 1024 as '数据大小(MB)',
    INDEX_LENGTH / 1024 / 1024 as '索引大小(MB)',
    TABLE_COLLATION as '字符集',
    CREATE_TIME as '创建时间',
    UPDATE_TIME as '更新时间'
FROM
    information_schema.TABLES 
WHERE
    TABLE_SCHEMA = 'ad_platform'
ORDER BY 
    (DATA_LENGTH + INDEX_LENGTH) DESC; 