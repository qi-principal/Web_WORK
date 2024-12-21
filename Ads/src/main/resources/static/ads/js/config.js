const AdConfig = {
    // API配置
    API: {
        TASK: {
            ACTIVE: '/api/ad-tasks/active',
            RECORD: '/api/ad-tasks/record'
        },
        ADVERTISEMENT: {
            GET: '/api/advertisements'
        }
    },

    // 广告配置
    AD: {
        // 广告刷新间隔（毫秒）
        REFRESH_INTERVAL: 5000,
        // 默认广告配置
        DEFAULT: {
            IMAGE: 'default/default.jpg',
            LINK: '#'
        }
    },

    // 任务状态
    TASK_STATUS: {
        PENDING: 'PENDING',
        RUNNING: 'RUNNING',
        COMPLETED: 'COMPLETED',
        FAILED: 'FAILED'
    }
};

window.AD_CONFIG = {
    domain: 'localhost:8080' // 替换为实际的广告域名
}; 