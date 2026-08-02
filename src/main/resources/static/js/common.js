/**
 * 公共JavaScript工具函数
 */

// API基础路径
const API_BASE = '';

/**
 * 封装fetch请求
 */
async function request(url, options = {}) {
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json'
        }
    };

    const mergedOptions = {
        ...defaultOptions,
        ...options,
        headers: {
            ...defaultOptions.headers,
            ...options.headers
        }
    };

    // DELETE请求不需要body，也不需要Content-Type
    if (options.method === 'DELETE') {
        delete mergedOptions.headers['Content-Type'];
    }

    if (mergedOptions.body && typeof mergedOptions.body === 'object') {
        mergedOptions.body = JSON.stringify(mergedOptions.body);
    }

    try {
        console.log('Request:', options.method || 'GET', API_BASE + url, mergedOptions);
        const response = await fetch(API_BASE + url, mergedOptions);

        // 检查响应状态
        if (!response.ok) {
            console.error('Response not ok:', response.status, response.statusText);
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log('Response:', data);

        // 处理未登录
        if (data.code === 401) {
            if (window.location.pathname.startsWith('/admin')) {
                window.location.href = '/admin/login';
            } else {
                window.location.href = '/login';
            }
            return null;
        }

        return data;
    } catch (error) {
        console.error('请求失败:', error);
        showMessage('网络请求失败', 'error');
        return null;
    }
}

/**
 * GET请求
 */
function get(url) {
    return request(url, { method: 'GET' });
}

/**
 * POST请求
 */
function post(url, data) {
    return request(url, { method: 'POST', body: data });
}

/**
 * PUT请求
 */
function put(url, data) {
    return request(url, { method: 'PUT', body: data });
}

/**
 * DELETE请求
 */
function del(url) {
    return request(url, { method: 'DELETE' });
}

/**
 * 显示消息提示
 */
function showMessage(text, type = 'success') {
    // 移除已有的消息
    const existingMsg = document.querySelector('.message');
    if (existingMsg) {
        existingMsg.remove();
    }

    const msg = document.createElement('div');
    msg.className = `message message-${type}`;
    msg.textContent = text;
    document.body.appendChild(msg);

    setTimeout(() => {
        msg.remove();
    }, 3000);
}

/**
 * 确认对话框 - 重命名以避免与原生 confirm 冲突
 */
function showConfirm(message) {
    return window.confirm(message);
}

/**
 * 格式化日期时间
 */
function formatDateTime(dateStr) {
    if (!dateStr) return '-';
    const date = new Date(dateStr);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hour = String(date.getHours()).padStart(2, '0');
    const minute = String(date.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day} ${hour}:${minute}`;
}

/**
 * 格式化日期
 */
function formatDate(dateStr) {
    if (!dateStr) return '-';
    const date = new Date(dateStr);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

/**
 * 相对时间
 */
function timeAgo(dateStr) {
    if (!dateStr) return '-';
    const date = new Date(dateStr);
    const now = new Date();
    const diff = now - date;

    const minute = 60 * 1000;
    const hour = 60 * minute;
    const day = 24 * hour;

    if (diff < minute) {
        return '刚刚';
    } else if (diff < hour) {
        return Math.floor(diff / minute) + '分钟前';
    } else if (diff < day) {
        return Math.floor(diff / hour) + '小时前';
    } else if (diff < 7 * day) {
        return Math.floor(diff / day) + '天前';
    } else {
        return formatDate(dateStr);
    }
}

/**
 * 格式化时间（时:分）
 */
function formatTime(dateStr) {
    if (!dateStr) return '-';
    const date = new Date(dateStr);
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hour = String(date.getHours()).padStart(2, '0');
    const minute = String(date.getMinutes()).padStart(2, '0');
    return `${month}-${day} ${hour}:${minute}`;
}

/**
 * 获取活动状态文本
 */
function getActivityStatusText(status) {
    const statusMap = {
        0: '待审核',
        1: '已通过',
        2: '已拒绝',
        3: '进行中',
        4: '已结束',
        5: '已取消'
    };
    return statusMap[status] || '未知';
}

/**
 * 获取活动状态样式类
 */
function getActivityStatusClass(status) {
    const classMap = {
        0: 'status-pending',
        1: 'status-approved',
        2: 'status-rejected',
        3: 'status-approved',
        4: 'status-disabled',
        5: 'status-disabled'
    };
    return classMap[status] || '';
}

/**
 * 获取报名状态文本
 */
function getSignupStatusText(status) {
    const statusMap = {
        0: '待审核',
        1: '已通过',
        2: '已拒绝',
        3: '已取消'
    };
    return statusMap[status] || '未知';
}

/**
 * 获取用户角色文本
 */
function getRoleText(role) {
    const roleMap = {
        1: '普通用户',
        2: '活动发起者',
        3: '管理员'
    };
    return roleMap[role] || '未知';
}

/**
 * 获取默认头像
 */
function getDefaultAvatar() {
    return '/static/images/default-avatar.svg';
}

/**
 * 获取默认封面图
 */
function getDefaultCover() {
    return '/static/images/default-cover.svg';
}

/**
 * 处理图片URL
 */
function getImageUrl(url) {
    if (!url) return getDefaultCover();
    if (url.startsWith('http')) return url;
    return url;
}

/**
 * 文件上传
 * @param {File} file 要上传的文件
 * @param {String} type 文件类型：category(分类图标)、banner(轮播图)、activity(活动图片)、avatar(用户头像)、common(通用)
 * @returns {Promise<String|null>} 上传成功返回URL，失败返回null
 */
async function uploadFile(file, type = 'common') {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('type', type);

    try {
        const response = await fetch('/api/common/upload', {
            method: 'POST',
            body: formData
        });
        const data = await response.json();

        if (data.code === 200) {
            return data.data;
        } else {
            showMessage(data.message || '上传失败', 'error');
            return null;
        }
    } catch (error) {
        console.error('上传失败:', error);
        showMessage('上传失败', 'error');
        return null;
    }
}

/**
 * 生成分页HTML
 */
function renderPagination(current, total, pageSize, onPageChange) {
    const totalPages = Math.ceil(total / pageSize);
    if (totalPages <= 1) return '';

    let html = '<div class="pagination">';

    // 上一页
    html += `<button class="page-btn" ${current <= 1 ? 'disabled' : ''} onclick="${onPageChange}(${current - 1})">上一页</button>`;

    // 页码
    for (let i = 1; i <= totalPages; i++) {
        if (i === 1 || i === totalPages || (i >= current - 2 && i <= current + 2)) {
            html += `<button class="page-btn ${i === current ? 'active' : ''}" onclick="${onPageChange}(${i})">${i}</button>`;
        } else if (i === current - 3 || i === current + 3) {
            html += '<span>...</span>';
        }
    }

    // 下一页
    html += `<button class="page-btn" ${current >= totalPages ? 'disabled' : ''} onclick="${onPageChange}(${current + 1})">下一页</button>`;

    html += '</div>';
    return html;
}

/**
 * 防抖函数
 */
function debounce(func, wait) {
    let timeout;
    return function (...args) {
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(this, args), wait);
    };
}

/**
 * 获取URL参数
 */
function getUrlParam(name) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(name);
}

/**
 * 获取路径中的ID
 */
function getPathId() {
    const path = window.location.pathname;
    const match = path.match(/\/(\d+)$/);
    return match ? parseInt(match[1]) : null;
}
