// 获取token
function getToken() {
    return localStorage.getItem('token');
}

// 添加token到请求头
function addAuthHeader(options) {
    const token = getToken();
    if (token) {
        options.headers = options.headers || {};
        options.headers['Authorization'] = 'Bearer ' + token;
    }
    return options;
}

// 加载广告位列表
function loadAdSpaces() {
    $.ajax(addAuthHeader({
        url: '/api/v1/website/adspaces',
        method: 'GET',
        success: function(response) {
            var html = '<div class="list-group">';
            response.forEach(function(adSpace) {
                html += `
                    <div class="list-group-item">
                        <h5>${adSpace.name}</h5>
                        <p>尺寸: ${adSpace.width}x${adSpace.height}</p>
                        <p>状态: ${getStatusText(adSpace.status)}</p>
                        <div class="mb-2">
                            <pre class="code-block"><code>${escapeHtml(adSpace.code)}</code></pre>
                            <button class="btn btn-sm btn-secondary copy-btn" data-code="${escapeHtml(adSpace.code)}">复制代码</button>
                        </div>
                        <div class="btn-group">
                            <button class="btn btn-sm btn-success approve-btn" data-id="${adSpace.id}">审核通过</button>
                            <button class="btn btn-sm btn-danger reject-btn" data-id="${adSpace.id}">拒绝</button>
                            <button class="btn btn-sm btn-primary preview-btn" data-code="${escapeHtml(adSpace.code)}">预览</button>
                        </div>
                    </div>
                `;
            });
            html += '</div>';
            $('#adSpaceList').html(html);
        },
        error: function(xhr) {
            if (xhr.status === 401) {
                window.location.href = '/login.html';
            } else {
                alert('加载广告位列表失败！');
            }
        }
    }));
}

// 创建广告位
function createAdSpace(formData) {
    $.ajax(addAuthHeader({
        url: '/api/v1/website/adspaces',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(formData),
        success: function(response) {
            alert('创建成功！');
            loadAdSpaces();
            $('#adSpaceForm')[0].reset();
        },
        error: function(xhr) {
            if (xhr.status === 401) {
                window.location.href = '/login.html';
            } else {
                alert('创建失败！');
            }
        }
    }));
}

// 审核通过
function approveAdSpace(id) {
    $.ajax(addAuthHeader({
        url: '/api/v1/website/adspaces/' + id + '/approve',
        method: 'POST',
        success: function() {
            alert('审核通过！');
            loadAdSpaces();
        },
        error: function(xhr) {
            if (xhr.status === 401) {
                window.location.href = '/login.html';
            } else {
                alert('操作失败！');
            }
        }
    }));
}

// 审核拒绝
function rejectAdSpace(id) {
    $.ajax(addAuthHeader({
        url: '/api/v1/website/adspaces/' + id + '/reject',
        method: 'POST',
        success: function() {
            alert('已拒绝！');
            loadAdSpaces();
        },
        error: function(xhr) {
            if (xhr.status === 401) {
                window.location.href = '/login.html';
            } else {
                alert('操作失败！');
            }
        }
    }));
}

// 获取状态文本
function getStatusText(status) {
    switch(status) {
        case 0: return '待审核';
        case 1: return '已启用';
        case 2: return '已拒绝';
        default: return '未知状态';
    }
}

// HTML转义
function escapeHtml(unsafe) {
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

// 复制文本到剪贴板
function copyToClipboard(text) {
    var textarea = document.createElement('textarea');
    textarea.value = text;
    document.body.appendChild(textarea);
    textarea.select();
    document.execCommand('copy');
    document.body.removeChild(textarea);
}

// 初始化事件监听
$(document).ready(function() {
    // 检查登录状态
    if (!getToken()) {
        window.location.href = '/login.html';
        return;
    }

    // 表单提交
    $('#adSpaceForm').submit(function(e) {
        e.preventDefault();
        var formData = {
            websiteId: $('input[name="websiteId"]').val(),
            name: $('input[name="name"]').val(),
            width: parseInt($('input[name="width"]').val()),
            height: parseInt($('input[name="height"]').val())
        };
        createAdSpace(formData);
    });

    // 审核按钮点击
    $(document).on('click', '.approve-btn', function() {
        var id = $(this).data('id');
        approveAdSpace(id);
    });

    $(document).on('click', '.reject-btn', function() {
        var id = $(this).data('id');
        rejectAdSpace(id);
    });

    // 复制代码按钮点击
    $(document).on('click', '.copy-btn', function() {
        var code = $(this).data('code');
        copyToClipboard(code);
        alert('代码已复制到剪贴板！');
    });

    // 预览按钮点击
    $(document).on('click', '.preview-btn', function() {
        var code = $(this).data('code');
        $('#previewContent').html(code);
        $('#previewContainer').show();
    });

    // 初始加载
    loadAdSpaces();
}); 