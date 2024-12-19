#!/bin/bash

# 创建备份目录
BACKUP_DIR="/backup/database"
mkdir -p $BACKUP_DIR

# 设置数据库连接信息
DB_USER="root"
DB_NAME="ad_platform"
BACKUP_FILE="$BACKUP_DIR/${DB_NAME}_$(date +%Y%m%d).sql"

# 执行备份
mysqldump -u $DB_USER -p $DB_NAME > $BACKUP_FILE

# 保留最近30天的备份，删除更早的备份
find $BACKUP_DIR -name "${DB_NAME}_*.sql" -mtime +30 -delete

echo "数据库备份完成：$BACKUP_FILE" 