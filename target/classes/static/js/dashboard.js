// API 基础URL
const BASE_URL = '/api/v1';

// 工具函数：获取token
function getToken() {
    return localStorage.getItem('token');
}

// 工具函数：检查登录状态
function checkAuth() {
    const token = getToken();
    if (!token) {
        window.location.href = '../index.html';
    }
}

// 工具函数：显示错误消息
function showError(message) {
    alert(message);
}

// 切换面板显示
function showPanel(panelId) {
    // 隐藏所有面板
    document.querySelectorAll('.content-panel').forEach(panel => {
        panel.classList.remove('active');
    });

    // 取消所有导航项的激活状态
    document.querySelectorAll('.sidebar-nav li').forEach(item => {
        item.classList.remove('active');
    });

    // 显示选中的面板
    const panel = document.getElementById(panelId);
    if (panel) {
        panel.classList.add('active');
    }

    // 激活对应的导航项
    const navItem = document.querySelector(`.sidebar-nav a[href="#${panelId}"]`);
    if (navItem) {
        navItem.parentElement.classList.add('active');
    }

    // 根据面板类型加载数据
    switch (panelId) {
        case 'overview':
            loadDashboardData();
            break;
        case 'ad':
            loadAds();
            loadCampaigns();
            loadPositions();
            break;
        case 'material':
            loadMaterials();
            break;
    }
}

// 退出登录
function logout() {
    localStorage.removeItem('token');
    window.location.href = '../index.html';
}

// 加载用户信息
async function loadUserInfo() {
    try {
        const response = await fetch(`${BASE_URL}/users/me`, {
            headers: {
                'Authorization': `Bearer ${getToken()}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            document.getElementById('currentUser').textContent = data.data.username;
        } else {
            showError('获取用户信息失败');
        }
    } catch (error) {
        showError('网络错误');
    }
}

// 加载统计数据
async function loadStats() {
    try {
        const response = await fetch(`${BASE_URL}/stats/overview`, {
            headers: {
                'Authorization': `Bearer ${getToken()}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            // 更新统计卡片数据
            updateStatsCards(data.data);
        }
    } catch (error) {
        showError('加载统计数据失败');
    }
}

// 更新统计卡片
function updateStatsCards(data) {
    // 根据不同的用户类型更新不同的统计数据
    if (document.getElementById('todayImpressions')) {
        document.getElementById('todayImpressions').textContent = data.impressions || 0;
        document.getElementById('todayClicks').textContent = data.clicks || 0;
        document.getElementById('ctr').textContent = ((data.clicks / data.impressions) * 100 || 0).toFixed(2) + '%';
    }
    
    // 更新收入/支出
    if (document.getElementById('todayEarnings')) {
        document.getElementById('todayEarnings').textContent = '¥' + (data.earnings || 0).toFixed(2);
    }
    if (document.getElementById('todaySpend')) {
        document.getElementById('todaySpend').textContent = '¥' + (data.spend || 0).toFixed(2);
    }
}

// 加载用户列表（管理员功能）
async function loadUserList() {
    try {
        const response = await fetch(`${BASE_URL}/users`, {
            headers: {
                'Authorization': `Bearer ${getToken()}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            const userList = document.getElementById('userList');
            if (userList) {
                userList.innerHTML = data.data.map(user => `
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.username}</td>
                        <td>${getUserTypeText(user.userType)}</td>
                        <td>${user.status === 1 ? '正常' : '禁用'}</td>
                        <td>
                            <button onclick="updateUserStatus(${user.id}, ${user.status === 1 ? 0 : 1})">
                                ${user.status === 1 ? '禁用' : '启用'}
                            </button>
                        </td>
                    </tr>
                `).join('');
            }
        }
    } catch (error) {
        showError('加载用户列表失败');
    }
}

// 更新用户状态
async function updateUserStatus(userId, status) {
    try {
        const response = await fetch(`${BASE_URL}/users/${userId}/status`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${getToken()}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ status })
        });

        if (response.ok) {
            loadUserList();
        } else {
            showError('更新用户状态失败');
        }
    } catch (error) {
        showError('网络错误');
    }
}

// 页面加载完成后执行
document.addEventListener('DOMContentLoaded', () => {
    loadDashboardData();
    setupNavigation();
});

// 设置导航
function setupNavigation() {
    // 从 URL 获取当前面板
    const hash = window.location.hash.slice(1) || 'overview';
    showPanel(hash);

    // 监听 URL 变化
    window.addEventListener('hashchange', () => {
        const hash = window.location.hash.slice(1);
        showPanel(hash);
    });
}

// 加载仪表盘数据
async function loadDashboardData() {
    try {
        const response = await fetch(`${BASE_URL}/dashboard/stats`, {
            headers: {
                'Authorization': `Bearer ${getToken()}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            updateDashboardStats(data.data);
            renderCharts(data.data);
        } else {
            showError('加载数据失败');
        }
    } catch (error) {
        showError('网络错误，请稍后重试');
    }
}

// 更新仪表盘统计数据
function updateDashboardStats(stats) {
    document.getElementById('todaySpend').textContent = stats.todaySpend.toFixed(2);
    document.getElementById('todayImpressions').textContent = stats.todayImpressions.toLocaleString();
    document.getElementById('todayClicks').textContent = stats.todayClicks.toLocaleString();
    document.getElementById('todayCtr').textContent = (stats.todayClicks / stats.todayImpressions * 100 || 0).toFixed(2);
}

// 渲染图表
function renderCharts(data) {
    renderSpendChart(data.spendTrend);
    renderPerformanceChart(data.performanceTrend);
}

// 渲染消费趋势图表
function renderSpendChart(trend) {
    // 这里使用你选择的图表库来渲染消费趋势图表
    // 例如：使用 ECharts 或 Chart.js
}

// 渲染效果趋势图表
function renderPerformanceChart(trend) {
    // 这里使用你选择的图表库来渲染效果趋势图表
    // 例如：使用 ECharts 或 Chart.js
} 