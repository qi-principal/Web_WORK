class LawNewsManager {
    constructor() {
        this.container = document.getElementById('lawNewsContainer');
        this.refreshBtn = document.getElementById('refreshBtn');
        this.newsData = [];
        this.init();
    }

    async init() {
        console.log('初始化LawNewsManager...');
        if (this.refreshBtn) {
            this.refreshBtn.addEventListener('click', () => this.refreshNews());
            console.log('刷新按钮事件监听器已添加');
        } else {
            console.error('未找到刷新按钮元素!');
        }
        await this.loadNews();
    }

    async loadNews() {
        try {
            console.log('开始获取法制新闻...');
            const response = await fetch('https://10142zp2193.goho.co/api/news/law');
            console.log('法制新闻API响应:', response);
            const result = await response.json();
            console.log('获取到的法制新闻数据:', result);
            
            if (result.code === 1 && Array.isArray(result.data)) {
                this.newsData = result.data;
                console.log('成功获取法制新闻，数量:', this.newsData.length);
                this.refreshNews();
            } else {
                console.error('获取法制新闻失败:', result.msg);
            }
        } catch (error) {
            console.error('获取法制新闻失败:', error);
        }
    }

    getRandomNews(count) {
        const randomNews = [...this.newsData]
            .sort(() => Math.random() - 0.5)
            .slice(0, count);
        console.log(`获取${count}条随机新闻:`, randomNews);
        return randomNews;
    }

    createNewsCard(news) {
        console.log('创建法制新闻卡片:', news);
        const defaultImage = 'https://via.placeholder.com/300x200?text=新闻图片';
        
        return `
            <article class="news-card">
                <img src="${news.imageUrl || defaultImage}" alt="${news.title}" onerror="this.src='${defaultImage}'"/>
                <div class="news-card-content">
                    <h3><a href="#" style="text-decoration: none;">${news.title}</a></h3>
                    <p>${news.brief || '暂无简介'}</p>
                    <div class="news-card-meta">
                        <span class="date">${this.formatDate(news.createTime)}</span>
                        <span class="category">${news.category || '法制'}</span>
                    </div>
                </div>
            </article>
        `;
    }

    createAdCard() {
        console.log('创建广告卡片');
        return `
            <article class="news-card ad-card">
                <div class="ad-label">广告</div>
                <iframe 
                    src="http://192.168.10.155:8080/#/ad-preview/12" 
                    style="width: 100%; height: 300px; border: none; overflow: hidden;"
                    loading="lazy"
                    title="广告内容"
                ></iframe>
            </article>
        `;
    }

    formatDate(dateString) {
        if (!dateString) return '未知时间';
        try {
            const date = new Date(dateString);
            return date.toLocaleDateString('zh-CN', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit'
            });
        } catch (error) {
            console.error('日期格式化错误:', error);
            return '日期格式错误';
        }
    }

    refreshNews() {
        console.log('开始刷新法制新闻...');
        
        if (!this.container) {
            console.error('未找到法制新闻容器元素!');
            return;
        }
        
        const randomNews = this.getRandomNews(10);
        
        const cards = [
            ...randomNews.map(news => this.createNewsCard(news)),
            this.createAdCard(),
            this.createAdCard()
        ];
        cards.sort(() => Math.random() - 0.5);
        
        this.container.innerHTML = cards.join('');
        console.log('法制新闻刷新完成');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    console.log('法制新闻页面DOM加载完成');
    new LawNewsManager();
});

setInterval(() => {
    console.log('执行定时刷新');
    document.getElementById('refreshBtn').click();
}, 300000);