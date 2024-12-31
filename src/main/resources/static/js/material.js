// 全局变量
let currentPage = 1;
let pageSize = 12;
let totalPages = 1;
let currentMaterial = null;

// 页面加载完成后执行
document.addEventListener('DOMContentLoaded', () => {
    loadMaterials();
    setupFileUpload();
});

// 加载素材列表
async function loadMaterials(page = 1) {
    try {
        const response = await fetch(`${BASE_URL}/materials?page=${page}&size=${pageSize}`, {
            headers: {
                'Authorization': `Bearer ${getToken()}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            renderMaterials(data.data.records);
            renderPagination(data.data.total);
        } else {
            showError('加载素材失败');
        }
    } catch (error) {
        showError('网络错误，请稍后重试');
    }
}

// 渲染素材列表
function renderMaterials(materials) {
    const grid = document.getElementById('materialGrid');
    grid.innerHTML = materials.map(material => `
        <div class="material-card" onclick="showPreview(${material.id})">
            <div class="material-preview">
                ${material.type === 1 
                    ? `<img src="${material.url}" alt="${material.name}">`
                    : `<video src="${material.url}" muted></video>`
                }
            </div>
            <div class="material-info">
                <h4>${material.name}</h4>
                <div class="material-meta">
                    <span>${formatFileSize(material.size)}</span>
                    <span>${formatDate(material.createTime)}</span>
                </div>
            </div>
        </div>
    `).join('');
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
    loadMaterials(page);
}

// 显示上传弹窗
function showUploadModal() {
    document.getElementById('materialModal').classList.add('active');
}

// 关闭上传弹窗
function closeModal() {
    document.getElementById('materialModal').classList.remove('active');
    document.getElementById('materialForm').reset();
    document.getElementById('filePreview').innerHTML = '<span>点击或拖拽文件到此处</span>';
}

// 设置文件上传
function setupFileUpload() {
    const fileInput = document.getElementById('fileInput');
    const preview = document.getElementById('filePreview');
    const form = document.getElementById('materialForm');

    // 处理文件选择
    fileInput.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file) {
            previewFile(file);
        }
    });

    // 处理拖放
    preview.addEventListener('dragover', (e) => {
        e.preventDefault();
        preview.style.borderColor = '#1890ff';
    });

    preview.addEventListener('dragleave', () => {
        preview.style.borderColor = '#d9d9d9';
    });

    preview.addEventListener('drop', (e) => {
        e.preventDefault();
        preview.style.borderColor = '#d9d9d9';
        const file = e.dataTransfer.files[0];
        if (file) {
            fileInput.files = e.dataTransfer.files;
            previewFile(file);
        }
    });

    // 处理表单提交
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = new FormData(form);
        
        try {
            const response = await fetch(`${BASE_URL}/materials/upload`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${getToken()}`
                },
                body: formData
            });

            if (response.ok) {
                closeModal();
                loadMaterials(currentPage);
                showMessage('上传成功');
            } else {
                const data = await response.json();
                showError(data.message || '上传失败');
            }
        } catch (error) {
            showError('网络错误，请稍后重试');
        }
    });
}

// 预览文件
function previewFile(file) {
    const preview = document.getElementById('filePreview');
    const reader = new FileReader();

    reader.onload = (e) => {
        if (file.type.startsWith('image/')) {
            preview.innerHTML = `<img src="${e.target.result}" alt="预览图片">`;
        } else if (file.type.startsWith('video/')) {
            preview.innerHTML = `<video src="${e.target.result}" controls></video>`;
        }
    };

    reader.readAsDataURL(file);
}

// 显示素材预览
async function showPreview(id) {
    try {
        const response = await fetch(`${BASE_URL}/materials/${id}`, {
            headers: {
                'Authorization': `Bearer ${getToken()}`
            }
        });

        if (response.ok) {
            const material = await response.json();
            currentMaterial = material.data;
            
            const modal = document.getElementById('previewModal');
            const container = document.getElementById('previewContainer');
            
            // 设置预览内容
            if (material.data.type === 1) {
                container.innerHTML = `<img src="${material.data.url}" alt="${material.data.name}">`;
            } else {
                container.innerHTML = `<video src="${material.data.url}" controls></video>`;
            }
            
            // 设置素材信息
            document.getElementById('previewFileName').textContent = material.data.name;
            document.getElementById('previewUploadTime').textContent = formatDate(material.data.createTime);
            document.getElementById('previewFileSize').textContent = formatFileSize(material.data.size);
            
            // 加载使用次数
            const usageResponse = await fetch(`${BASE_URL}/materials/${id}/ads`, {
                headers: {
                    'Authorization': `Bearer ${getToken()}`
                }
            });
            
            if (usageResponse.ok) {
                const usageData = await usageResponse.json();
                document.getElementById('previewUsageCount').textContent = usageData.data.length;
            }
            
            modal.classList.add('active');
        } else {
            showError('加载素材详情失败');
        }
    } catch (error) {
        showError('网络错误，请稍后重试');
    }
}

// 关闭预览弹窗
function closePreviewModal() {
    document.getElementById('previewModal').classList.remove('active');
    currentMaterial = null;
}

// 删除素材
async function deleteMaterial() {
    if (!currentMaterial) return;
    
    if (!confirm('确定要删除这个素材吗？')) return;
    
    try {
        const response = await fetch(`${BASE_URL}/materials/${currentMaterial.id}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${getToken()}`
            }
        });

        if (response.ok) {
            closePreviewModal();
            loadMaterials(currentPage);
            showMessage('删除成功');
        } else {
            showError('删除失败');
        }
    } catch (error) {
        showError('网络错误，请稍后重试');
    }
}

// 筛选素材
function filterMaterials() {
    const type = document.getElementById('materialType').value;
    const keyword = document.getElementById('searchInput').value;
    
    // 重置分页并加载
    currentPage = 1;
    loadMaterials();
}

// 工具函数：格式化文件大小
function formatFileSize(bytes) {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
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