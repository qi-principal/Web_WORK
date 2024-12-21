-- 添加点击链接字段
ALTER TABLE advertisement
ADD COLUMN click_url VARCHAR(255) COMMENT '点击链接' AFTER end_time; 