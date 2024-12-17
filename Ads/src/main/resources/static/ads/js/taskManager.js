const TaskManager = {
    // 当前任务列表
    tasks: [],
    
    // 初始化
    init: function() {
        this.loadTasks();
        // 定期刷新任务列表
        setInterval(() => this.loadTasks(), AdConfig.AD.REFRESH_INTERVAL);
    },

    // 加载任务列表
    loadTasks: async function() {
        try {
            const response = await fetch(AdConfig.API.TASK.ACTIVE);
            if (!response.ok) throw new Error('Failed to load tasks');
            
            const tasks = await response.json();
            this.tasks = this.sortTasks(tasks);
            
            // 通知广告管理器更新广告
            if (window.parent.AdManager) {
                window.parent.AdManager.updateAds(this.tasks);
            }
        } catch (error) {
            console.error('Error loading tasks:', error);
            this.tasks = [];
        }
    },

    // 按创建时间排序任务
    sortTasks: function(tasks) {
        return tasks.sort((a, b) => {
            return new Date(b.createdAt) - new Date(a.createdAt);
        });
    },

    // 获取活动任务
    getActiveTasks: function() {
        return this.tasks.filter(task => 
            task.taskStatus === AdConfig.TASK_STATUS.RUNNING
        );
    },

    // 记录任务展示
    recordTaskDisplay: async function(taskId) {
        try {
            const response = await fetch(AdConfig.API.TASK.RECORD, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ taskId })
            });
            
            if (!response.ok) throw new Error('Failed to record task display');
        } catch (error) {
            console.error('Error recording task display:', error);
        }
    },

    // 检查是否所有任务都已完成
    areAllTasksCompleted: function() {
        return this.tasks.length > 0 && 
               this.tasks.every(task => 
                   task.taskStatus === AdConfig.TASK_STATUS.COMPLETED ||
                   task.taskStatus === AdConfig.TASK_STATUS.FAILED
               );
    }
}; 