class NewsManager {
    constructor() {
        this.newsContainer = document.getElementById('newsContainer');
        this.trendingContainer = document.getElementById('trendingContainer');
        this.refreshBtn = document.getElementById('refreshBtn');
        this.newsData = [];
        this.init();
    }

    async init() {
        console.log('初始化NewsManager...');
        this.refreshBtn.addEventListener('click', () => this.refreshNews());
        await this.loadNews();
    }

    async loadNews() {
        try {
            console.log('开始加载新闻数据...');
            const response = await fetch('https://10142zp2193.goho.co/api/news/all');
            console.log('API响应:', response);
            const result = await response.json();
            console.log('获取到的新闻数据:', result);
            
            if (result.code === 1 && Array.isArray(result.data)) {
                this.newsData = result.data;
                console.log('成功加载新闻数据，数量:', this.newsData.length);
                this.refreshNews();
                this.updateTrending();
            } else {
                console.error('加载新闻失败:', result.msg);
            }
        } catch (error) {
            console.error('获取新闻数据出错:', error);
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
        console.log('创建新闻卡片:', news);
        const defaultImage = 'https://via.placeholder.com/300x200?text=新闻图片';
        const defaultBrief = '暂无简介';
        
        return `
            <article class="news-card">
                <img src="${news.imageUrl || defaultImage}" alt="${news.title}" onerror="this.src='${defaultImage}'"/>
                <div class="news-card-content">
                    <h3><a href="#" style="text-decoration: none;">${news.title}</a></h3>
                    <p>${news.brief || defaultBrief}</p>
                    <div class="news-card-meta">
                        <span>${this.formatDate(news.createTime)}</span>
                        <span class="category">${news.category || '未分类'}</span>
                    </div>
                </div>
            </article>
        `;
    }

    createTrendingCard(news) {
        console.log('创建热门新闻卡片:', news);
        const defaultImage = 'https://via.placeholder.com/300x150?text=新闻图片';
        return `
            <article class="trending-card">
                <img src="${news.imageUrl || defaultImage}" alt="${news.title}" onerror="this.src='${defaultImage}'"/>
                <div class="trending-card-content">
                    <h4><a href="#" style="text-decoration: none;">${news.title}</a></h4>
                    <div class="trending-card-meta">
                        <span>${this.formatDate(news.createTime)}</span>
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
        console.log('刷新新闻显示...');
        const randomNews = this.getRandomNews(7);
        const cards = [...randomNews.map(news => this.createNewsCard(news)), this.createAdCard()];
        cards.sort(() => Math.random() - 0.5);
        this.newsContainer.innerHTML = cards.join('');
        console.log('新闻显示已更新');
    }

    updateTrending() {
        console.log('更新热门新闻...');
        const trendingNews = this.getRandomNews(3);
        this.trendingContainer.innerHTML = trendingNews
            .map(news => this.createTrendingCard(news))
            .join('');
        console.log('热门新闻已更新');
    }
}

// 等待DOM加载完成后初始化
document.addEventListener('DOMContentLoaded', () => {
    console.log('DOM加载完成，初始化NewsManager...');
    new NewsManager();
}); 