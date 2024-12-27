-- 初始化管理员账号
-- 用户名: admin
-- 密码: admin123
-- 注意：密码使用 BCrypt 加密，这里使用的是 'admin123' 加密后的值

INSERT INTO user (
    username,
    password,
    email,
    user_type,
    status,
    create_time,
    update_time
)
VALUES (
    'admin',
    '$2a$10$MgGE5bd/vGVf3/whWW1eiu8g6P631u/RSwEI7LACYOxvHwqnRRkL2',  -- '123456' 的 BCrypt 加密值
    'admin@adplatform.com',
    3,  -- 3 表示管理员
    1,  -- 1 表示正常状态
    NOW(),
    NOW()
)
ON DUPLICATE KEY UPDATE
    update_time = NOW();
