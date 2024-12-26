/**
 * 通用搜索功能实现
 * 搜索时跳转到result.html页面展示结果
 */

function searchNews() {
    const searchInput = document.getElementById('searchInput');
    const searchTerm = searchInput.value.trim();
    
    if (searchTerm) {
        // 跳转到结果页面，将搜索关键词作为参数传递
        window.location.href = `result.html?keyword=${encodeURIComponent(searchTerm)}`;
    }
}

// 添加回车键搜索功能
document.addEventListener('DOMContentLoaded', () => {
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keypress', function(event) {
            if (event.key === 'Enter') {
                searchNews();
            }
        });
    }
}); 