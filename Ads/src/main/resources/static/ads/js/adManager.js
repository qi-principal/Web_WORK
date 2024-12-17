const AdManager = {
    // 当前显示的广告
    currentAd: null,
    // 广告轮播定时器
    rotationTimer: null,

    // 初始化
    init: function() {
        this.setupEventListeners();
        this.startAdRotation();
    },

    // 设置事件监听
    setupEventListeners: function() {
        // 监听广告点击
        document.getElementById('dynamicAd').addEventListener('click', (e) => {
            if (this.currentAd) {
                this.recordAdClick(this.currentAd.taskId);
            }
        });
    },

    // 开始广告轮播
    startAdRotation: function() {
        if (this.rotationTimer) {
            clearInterval(this.rotationTimer);
        }
        
        this.rotationTimer = setInterval(() => {
            this.rotateAd();
        }, AdConfig.AD.REFRESH_INTERVAL);
    },

    // 更新广告列表

    updateAds: async function(tasks) {
        if (tasks.length === 0 || TaskManager.areAllTasksCompleted()) {
            this.showDefaultAd();
            return;
        }

        const activeTasks = TaskManager.getActiveTasks();
        if (activeTasks.length > 0) {
            await this.loadAndShowAd(activeTasks[0]);
        }
    },

    // 加载并显示广告
    loadAndShowAd: async function(task) {
        try {
            const response = await fetch(`${AdConfig.API.ADVERTISEMENT.GET}/${task.adId}`);
            if (!response.ok) throw new Error('Failed to load advertisement');
            
            const ad = await response.json();
            this.displayAd(ad, task);
            TaskManager.recordTaskDisplay(task.taskId);
        } catch (error) {
            console.error('Error loading advertisement:', error);
            this.showDefaultAd();
        }
    },

    // 显示广告
    displayAd: function(ad, task) {
        const dynamicAdElement = document.getElementById('dynamicAd');
        const defaultAdElement = document.getElementById('defaultAd');
        
        // 更新动态广告内容
        dynamicAdElement.innerHTML = `
            <a href="${ad.link}" target="_blank" rel="noopener noreferrer">
                <img src="${ad.imageUrl}" alt="${ad.title}">
                <div class="ad-text">
                    <div class="ad-title">${ad.title}</div>
                    <div class="ad-description">${ad.description}</div>
                </div>
            </a>
        `;
        
        // 显示动态广告，隐藏默认广告
        dynamicAdElement.style.display = 'block';
        defaultAdElement.style.display = 'none';
        
        this.currentAd = { ...ad, taskId: task.taskId };
    },

    // 显示默认广告
    showDefaultAd: function() {
        const dynamicAdElement = document.getElementById('dynamicAd');
        const defaultAdElement = document.getElementById('defaultAd');
        
        // 显示默认广告，隐藏动态广告
        dynamicAdElement.style.display = 'none';
        defaultAdElement.style.display = 'block';
        
        this.currentAd = null;
    },

    // 记录广告点击
    recordAdClick: async function(taskId) {
        try {
            const response = await fetch(`${AdConfig.API.TASK.RECORD}/click`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ taskId })
            });
            
            if (!response.ok) throw new Error('Failed to record ad click');
        } catch (error) {
            console.error('Error recording ad click:', error);
        }
    },

    // 轮换广告
    rotateAd: function() {
        const activeTasks = TaskManager.getActiveTasks();
        if (activeTasks.length === 0) {
            this.showDefaultAd();
            return;
        }

        // 找到当前广告的下一个广告
        let nextTaskIndex = 0;
        if (this.currentAd) {
            const currentIndex = activeTasks.findIndex(task => task.taskId === this.currentAd.taskId);
            nextTaskIndex = (currentIndex + 1) % activeTasks.length;
        }

        this.loadAndShowAd(activeTasks[nextTaskIndex]);
    }
}; 