// API 基础URL
const BASE_URL = '/api/v1';
const STATIC_BASE = '/api';  // 添加静态资源基础路径

// 工具函数：显示错误消息
function showError(message) {
    alert(message);
}

// 工具函数：保存token到localStorage
function saveToken(token) {
    localStorage.setItem('token', token);
}

// 工具函数：获取token
function getToken() {
    return localStorage.getItem('token');
}

// 工具函数：检查是否已登录
function checkAuth() {
    const token = getToken();
    if (!token && !window.location.href.includes('index.html')) {
        window.location.replace(`${STATIC_BASE}/index.html`);
    }
}

// 工具函数：获取用户类型文本
function getUserTypeText(userType) {
    switch (parseInt(userType)) {
        case 1:
            return '广告主';
        case 2:
            return '网站主';
        case 3:
            return '管理员';
        default:
            return '未知';
    }
}

// 工具函数：根据用户类型获取对应的控制面板URL
function getDashboardUrl(userType) {
    switch (parseInt(userType)) {
        case 1:
            return `${STATIC_BASE}/advertiser/dashboard.html`;  // 广告主
        case 2:
            return `${STATIC_BASE}/publisher/dashboard.html`;   // 网站主
        case 3:
            return `${STATIC_BASE}/admin/dashboard.html`;       // 管理员
        default:
            return `${STATIC_BASE}/index.html`;
    }
}

// 登录表单处理
if (document.getElementById('loginForm')) {
    document.getElementById('loginForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        console.log('登录请求:', username, password); // 调试用

        try {
            // 第一步：登录获取token
            const loginResponse = await fetch(`${BASE_URL}/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, password })
            });

            console.log('登录响应:', loginResponse); // 调试用

            const loginData = await loginResponse.json();
            if (loginResponse.ok) {
                saveToken(loginData.data.token);
                
                // 第二步：获取用户信息
                const userResponse = await fetch(`${BASE_URL}/users/me`, {
                    headers: {
                        'Authorization': `Bearer ${loginData.data.token}`
                    }
                });

                console.log('用户信息响应:', userResponse); // 调试用

                const userData = await userResponse.json();
                if (userResponse.ok) {
                    // 根据用户类型跳转到对应的控制面板
                    const dashboardUrl = getDashboardUrl(userData.data.userType);
                    console.log('跳转到:', dashboardUrl); // 调试用
                    window.location.replace(dashboardUrl); // 使用 replace 而不是 href
                } else {
                    showError('获取用户信息失败');
                }
            } else {
                showError(loginData.message || '登录失败');
            }
        } catch (error) {
            console.error('登录错误:', error); // 调试用
            showError('网络错误，请稍后重试');
        }
    });
}

// 注册表单处理
if (document.getElementById('registerForm')) {
    document.getElementById('registerForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        const email = document.getElementById('email').value;
        const userType = document.getElementById('userType').value;

        if (!userType) {
            showError('请选择用户类型');
            return;
        }

        try {
            const response = await fetch(`${BASE_URL}/auth/register`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ 
                    username, 
                    password, 
                    email,
                    userType
                })
            });

            const data = await response.json();
            if (response.ok) {
                alert('注册成功，请登录');
                window.location.replace(`${STATIC_BASE}/index.html`);
            } else {
                showError(data.message || '注册失败');
            }
        } catch (error) {
            showError('网络错误，请稍后重试');
        }
    });
}

// 加载用户信息
async function loadUserInfo() {
    try {
        const response = await fetch(`${BASE_URL}/users/me`, {
            headers: {
                'Authorization': `Bearer ${getToken()}`
            }
        });

        const data = await response.json();
        if (response.ok) {
            // 如果在用户信息页面，则显示用户信息
            if (document.getElementById('userUsername')) {
                document.getElementById('userUsername').textContent = data.data.username;
                document.getElementById('userEmail').textContent = data.data.email;
                document.getElementById('userStatus').textContent = data.data.status === 1 ? '正常' : '禁用';
            }
            // 如果在控制面板页面，则显示用户名
            if (document.getElementById('currentUser')) {
                document.getElementById('currentUser').textContent = data.data.username;
            }
        } else {
            showError(data.message || '获取用户信息失败');
            // 如果获取用户信息失败，可能是token无效，跳转到登录页
            window.location.replace(`${STATIC_BASE}/index.html`);
        }
    } catch (error) {
        showError('网络错误，请稍后重试');
        window.location.replace(`${STATIC_BASE}/index.html`);
    }
}

// 退出登录
function logout() {
    localStorage.removeItem('token');
    window.location.replace(`${STATIC_BASE}/index.html`);
}

// 在用户页面加载用户信息
if (window.location.href.includes('user.html')) {
    checkAuth();
    loadUserInfo();
} 