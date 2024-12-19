// 页面加载完成后检查登录状态
document.addEventListener('DOMContentLoaded', checkLoginStatus);

// 检查登录状态
async function checkLoginStatus() {
    const token = localStorage.getItem('token');
    if (token) {
        try {
            const response = await userApi.getCurrentUser();
            if (response.code === 200) {
                showUserInfo(response.data);
                document.getElementById('userInfoTab').style.display = 'block';
                showTab('userInfo');
            } else {
                localStorage.removeItem('token');
                showTab('login');
            }
        } catch (error) {
            localStorage.removeItem('token');
            showTab('login');
        }
    }
}

// 显示指定标签页
function showTab(tabId) {
    // 隐藏所有标签页
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.remove('active');
    });
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });

    // 显示选中的标签页
    document.getElementById(tabId).classList.add('active');
    document.querySelector(`button[onclick="showTab('${tabId}')"]`).classList.add('active');
}

// 处理登录
async function handleLogin(event) {
    event.preventDefault();
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;

    try {
        const response = await userApi.login({ username, password });
        if (response.code === 200) {
            localStorage.setItem('token', response.data.token);
            showMessage('登录成功', 'success');
            showUserInfo(response.data.user);
            document.getElementById('userInfoTab').style.display = 'block';
            showTab('userInfo');
        } else {
            showMessage(response.message || '登录失败', 'error');
        }
    } catch (error) {
        showMessage('登录失败', 'error');
    }
}

// 处理注册
async function handleRegister(event) {
    event.preventDefault();
    const data = {
        username: document.getElementById('registerUsername').value,
        password: document.getElementById('registerPassword').value,
        email: document.getElementById('registerEmail').value,
        phone: document.getElementById('registerPhone').value,
        userType: parseInt(document.getElementById('registerUserType').value)
    };
    //添加点东西
    console.log("username为"+data.username)
    console.log("password为"+data.password)
    console.log("email为"+data.email)
    console.log("phone为"+data.phone)
    console.log("userType为"+data.userType)
    

    try {
        const response = await userApi.register(data);
        if (response.code === 200) {
            showMessage('注册成功', 'success');
            showTab('login');
            document.getElementById('registerForm').reset();
        } else {
            showMessage(response.message || '注册失败', 'error');
        }
    } catch (error) {
        showMessage('注册失败', 'error');
    }
}

// 处理退出登录
function handleLogout() {
    localStorage.removeItem('token');
    document.getElementById('userInfoTab').style.display = 'none';
    showTab('login');
    showMessage('已退出登录', 'success');
}

// 显示用户信息
function showUserInfo(user) {
    const userTypeMap = {
        1: '广告主',
        2: '网站主',
        3: '管理员'
    };

    const statusMap = {
        0: '禁用',
        1: '启用'
    };

    const content = `
        <p><strong>用户名：</strong>${user.username}</p>
        <p><strong>邮箱：</strong>${user.email}</p>
        <p><strong>手机号：</strong>${user.phone || '未设置'}</p>
        <p><strong>用户类型：</strong>${userTypeMap[user.userType]}</p>
        <p><strong>账户状态：</strong>${statusMap[user.status]}</p>
        <p><strong>账户余额：</strong>${user.balance || 0} 元</p>
        <p><strong>注册时间：</strong>${new Date(user.createTime).toLocaleString()}</p>
    `;
    document.getElementById('userInfoContent').innerHTML = content;
}

// 显示消息提示
function showMessage(message, type) {
    const messageElement = document.getElementById('message');
    messageElement.textContent = message;
    messageElement.className = `message ${type}`;
    messageElement.style.display = 'block';

    setTimeout(() => {
        messageElement.style.display = 'none';
    }, 3000);
} 