// 全局变量
let currentPage = 1;
let pageSize = 10;
let totalPages = 1;
let selectedMaterialId = null;

// 页面加载完成后执行
document.addEventListener('DOMContentLoaded', () => {
    loadAds();
    loadCampaigns();
    loadPositions();
    setupAdForm();
});

// 加载广告列表
async function loadAds(page = 1) {
    try {
        const campaignId = document.getElementById('campaignFilter').value;
        const status = document.getElementById('statusFilter').value;
        const keyword = document.getElementById('searchInput').value;

        const response = await fetch(`${BASE_URL}/ads?page=${page}&size=${pageSize}&campaignId=${campaignId}&status=${status}&keyword=${keyword}`, {
            headers: {
                'Authorization': `Bearer ${getToken()}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            renderAds(data.data.records);
            renderPagination(data.data.total);
        } else {
            showError('加载广告列表失败');
        }
    } catch (error) {
        showError('网络错误，请稍后重试');
    }
}

// 渲染广告列表
function renderAds(ads) {
    const tbody = document.getElementById('adTableBody');
    tbody.innerHTML = ads.map(ad => `
        <tr>
            <td>${ad.name}</td>
            <td>${ad.campaignName}</td>
            <td>${ad.positionName}</td>
            <td>
                <span class="status-tag ${getStatusClass(ad.status)}">
                    ${getStatusText(ad.status)}
                </span>
            </td>
            <td>${formatDate(ad.startTime)}</td>
            <td>${formatDate(ad.endTime)}</td>
            <td>${ad.impressions}/${ad.clicks}</td>
            <td>
                ${renderActionButtons(ad)}
            </td>
        </tr>
    `).join('');
}

// 获取状态样式类名
function getStatusClass(status) {
    const statusMap = {
        1: 'status-pending',
        2: 'status-active',
        3: 'status-paused',
        4: 'status-rejected',
        5: 'status-ended'
    };
    return statusMap[status] || '';
}

// 获取状态文本
function getStatusText(status) {
    const statusMap = {
        1: '待审核',
        2: '投放中',
        3: '已暂停',
        4: '已拒绝',
        5: '已结束'
    };
    return statusMap[status] || '未知状态';
}

// 渲染操作按钮
function renderActionButtons(ad) {
    let buttons = [];
    
    // 添加详情按钮
    buttons.push(`<button class="action-btn" onclick="showAdDetail(${ad.id})">详情</button>`);
    
    // 根据状态添加其他操作按钮
    switch (ad.status) {
        case 2: // 投放中
            buttons.push(`<button class="action-btn" onclick="pauseAd(${ad.id})">暂停</button>`);
            break;
        case 3: // 已暂停
            buttons.push(`<button class="action-btn" onclick="resumeAd(${ad.id})">恢复</button>`);
            break;
        case 4: // 已拒绝
            buttons.push(`<button class="action-btn" onclick="showCreateAdModal(${ad.id})">编辑</button>`);
            break;
    }
    
    return buttons.join('');
}

// 渲染分页
function renderPagination(total) {
    totalPages = Math.ceil(total / pageSize);
    const pagination = document.getElementById('pagination');
    
    let html = '';
    if (currentPage > 1) {
        html += `<button onclick="changePage(${currentPage - 1})">上一页</button>`;
    }
    
    for (let i = 1; i <= totalPages; i++) {
        if (i === currentPage) {
            html += `<button class="active">${i}</button>`;
        } else if (i === 1 || i === totalPages || (i >= currentPage - 2 && i <= currentPage + 2)) {
            html += `<button onclick="changePage(${i})">${i}</button>`;
        } else if (i === currentPage - 3 || i === currentPage + 3) {
            html += `<span>...</span>`;
        }
    }
    
    if (currentPage < totalPages) {
        html += `<button onclick="changePage(${currentPage + 1})">下一页</button>`;
    }
    
    pagination.innerHTML = html;
}

// 切换页面
function changePage(page) {
    currentPage = page;
    loadAds(page);
}

// 加载广告活动列表
async function loadCampaigns() {
    try {
        const response = await fetch(`${BASE_URL}/campaigns`, {
            headers: {
                'Authorization': `Bearer ${getToken()}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            renderCampaignOptions(data.data);
        } else {
            showError('加载广告活动失败');
        }
    } catch (error) {
        showError('网络错误，请稍后重试');
    }
}

// 渲染广告活动选项
function renderCampaignOptions(campaigns) {
    const filterSelect = document.getElementById('campaignFilter');
    const formSelect = document.querySelector('select[name="campaignId"]');
    
    const options = campaigns.map(campaign => 
        `<option value="${campaign.id}">${campaign.name}</option>`
    ).join('');
    
    filterSelect.innerHTML += options;
    formSelect.innerHTML += options;
}

// 加载广告位置列表
async function loadPositions() {
    try {
        const response = await fetch(`${BASE_URL}/positions`, {
            headers: {
                'Authorization': `Bearer ${getToken()}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            renderPositionOptions(data.data);
        } else {
            showError('加载广告位置失败');
        }
    } catch (error) {
        showError('网络错误，请稍后重试');
    }
}

// 渲染广告位置选项
function renderPositionOptions(positions) {
    const select = document.querySelector('select[name="positionId"]');
    select.innerHTML += positions.map(position => 
        `<option value="${position.id}">${position.name}</option>`
    ).join('');
}

// 显示创建广告弹窗
function showCreateAdModal(adId = null) {
    document.getElementById('adModal').classList.add('active');
    if (adId) {
        loadAdDetails(adId);
    }
}

// 关闭创建广告弹窗
function closeAdModal() {
    document.getElementById('adModal').classList.remove('active');
    document.getElementById('adForm').reset();
    document.getElementById('selectedMaterial').innerHTML = '';
    selectedMaterialId = null;
}

// 设置广告表单
function setupAdForm() {
    const form = document.getElementById('adForm');
    
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        if (!selectedMaterialId) {
            showError('请选择广告素材');
            return;
        }
        
        const formData = new FormData(form);
        const data = {
            name: formData.get('name'),
            campaignId: formData.get('campaignId'),
            positionId: formData.get('positionId'),
            materialId: selectedMaterialId,
            startTime: formData.get('startTime'),
            endTime: formData.get('endTime'),
            deviceTypes: Array.from(formData.getAll('deviceType')).map(Number)
        };
        
        try {
            const response = await fetch(`${BASE_URL}/ads`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${getToken()}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                closeAdModal();
                loadAds(currentPage);
                showMessage('广告创建成功，等待审核');
            } else {
                const errorData = await response.json();
                showError(errorData.message || '创建广告失败');
            }
        } catch (error) {
            showError('网络错误，请稍后重试');
        }
    });
}

// 显示素材选择器
function showMaterialSelector() {
    document.getElementById('materialSelectorModal').classList.add('active');
    loadMaterials();
}

// 关闭素材选择器
function closeMaterialSelector() {
    document.getElementById('materialSelectorModal').classList.remove('active');
}

// 选择素材
function selectMaterial(material) {
    selectedMaterialId = material.id;
    const preview = document.getElementById('selectedMaterial');
    
    preview.innerHTML = `
        <div class="material-preview">
            ${material.type === 1 
                ? `<img src="${material.url}" alt="${material.name}">`
                : `<video src="${material.url}" controls></video>`
            }
        </div>
        <p>${material.name}</p>
    `;
    
    closeMaterialSelector();
}

// 暂停广告
async function pauseAd(id) {
    if (!confirm('确定要暂停这个广告吗？')) return;
    
    try {
        const response = await fetch(`${BASE_URL}/ads/${id}/pause`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${getToken()}`
            }
        });

        if (response.ok) {
            loadAds(currentPage);
            showMessage('广告已暂停');
        } else {
            showError('暂停广告失败');
        }
    } catch (error) {
        showError('网络错误，请稍后重试');
    }
}

// 恢复广告
async function resumeAd(id) {
    if (!confirm('确定要恢复这个广告吗？')) return;
    
    try {
        const response = await fetch(`${BASE_URL}/ads/${id}/resume`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${getToken()}`
            }
        });

        if (response.ok) {
            loadAds(currentPage);
            showMessage('广告已恢复');
        } else {
            showError('恢复广告失败');
        }
    } catch (error) {
        showError('网络错误，请稍后重试');
    }
}

// 加载广告详情
async function loadAdDetails(id) {
    try {
        const response = await fetch(`${BASE_URL}/ads/${id}`, {
            headers: {
                'Authorization': `Bearer ${getToken()}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            fillAdForm(data.data);
        } else {
            showError('加载广告详情失败');
        }
    } catch (error) {
        showError('网络错误，请稍后重试');
    }
}

// 填充广告表单
function fillAdForm(ad) {
    const form = document.getElementById('adForm');
    form.querySelector('[name="name"]').value = ad.name;
    form.querySelector('[name="campaignId"]').value = ad.campaignId;
    form.querySelector('[name="positionId"]').value = ad.positionId;
    form.querySelector('[name="startTime"]').value = formatDateTime(ad.startTime);
    form.querySelector('[name="endTime"]').value = formatDateTime(ad.endTime);
    
    // 设置设备类型
    ad.deviceTypes.forEach(type => {
        form.querySelector(`[name="deviceType"][value="${type}"]`).checked = true;
    });
    
    // 设置素材预览
    selectedMaterialId = ad.materialId;
    const preview = document.getElementById('selectedMaterial');
    preview.innerHTML = `
        <div class="material-preview">
            ${ad.materialType === 1 
                ? `<img src="${ad.materialUrl}" alt="${ad.name}">`
                : `<video src="${ad.materialUrl}" controls></video>`
            }
        </div>
        <p>${ad.materialName}</p>
    `;
}

// 筛选广告
function filterAds() {
    currentPage = 1;
    loadAds();
}

// 工具函数：格式化日期时间
function formatDateTime(dateString) {
    const date = new Date(dateString);
    return date.toISOString().slice(0, 16);
}

// 工具函数：格式化日期
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// 工具函数：显示成功消息
function showMessage(message) {
    alert(message); // 可以替换为更好的提示UI
}

// 工具函数：显示错误消息
function showError(message) {
    alert(message); // 可以替换为更好的错误提示UI
}

// 显示广告详情
async function showAdDetail(id) {
    try {
        const response = await fetch(`${BASE_URL}/ads/${id}`, {
            headers: {
                'Authorization': `Bearer ${getToken()}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            fillAdDetail(data.data);
            document.getElementById('adDetailModal').classList.add('active');
        } else {
            showError('加载广告详情失败');
        }
    } catch (error) {
        showError('网络错误，请稍后重试');
    }
}

// 填充广告详情
function fillAdDetail(ad) {
    // 基本信息
    document.getElementById('detailName').textContent = ad.name;
    document.getElementById('detailCampaign').textContent = ad.campaignName;
    document.getElementById('detailPosition').textContent = ad.positionName;
    document.getElementById('detailTimeRange').textContent = `${formatDate(ad.startTime)} 至 ${formatDate(ad.endTime)}`;
    document.getElementById('detailDevices').textContent = getDeviceTypes(ad.deviceTypes);
    
    // 投放数据
    document.getElementById('detailImpressions').textContent = ad.impressions.toLocaleString();
    document.getElementById('detailClicks').textContent = ad.clicks.toLocaleString();
    document.getElementById('detailCtr').textContent = ((ad.clicks / ad.impressions * 100) || 0).toFixed(2) + '%';
    
    // 审核信息
    document.getElementById('detailStatus').innerHTML = `<span class="status-tag ${getStatusClass(ad.status)}">${getStatusText(ad.status)}</span>`;
    document.getElementById('detailComment').textContent = ad.comment || '无';
    
    // 加载预览
    loadAdPreview(ad);
}

// 加载广告预览
function loadAdPreview(ad) {
    const iframe = document.getElementById('adPreviewFrame');
    // 根据广告位置类型设置预览框架的尺寸
    iframe.style.width = ad.position.width + 'px';
    iframe.style.height = ad.position.height + 'px';
    
    // 构建预览URL
    const previewUrl = `${BASE_URL}/ads/${ad.id}/preview`;
    iframe.src = previewUrl;
}

// 关闭广告详情弹窗
function closeAdDetailModal() {
    document.getElementById('adDetailModal').classList.remove('active');
    document.getElementById('adPreviewFrame').src = 'about:blank';
}

// 获取设备类型文本
function getDeviceTypes(types) {
    const deviceMap = {
        1: 'PC端',
        2: '移动端'
    };
    return types.map(type => deviceMap[type]).join('、');
} 