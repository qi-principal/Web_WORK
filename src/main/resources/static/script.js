class NewsManager {
    constructor() {
        this.newsContainer = document.getElementById('newsContainer');
        this.trendingContainer = document.getElementById('trendingContainer');
        this.refreshBtn = document.getElementById('refreshBtn');
        this.newsData = [];
        this.init();
    }

    async init() {
        this.refreshBtn.addEventListener('click', () => this.refreshNews());
        await this.loadNews();
    }

    async loadNews() {
        try {
            const response = await fetch('http://localhost:8080/news/all');
            const result = await response.json();
            
            if (result.code === 1 && Array.isArray(result.data)) {
                this.newsData = result.data;
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
        return [...this.newsData]
            .sort(() => Math.random() - 0.5)
            .slice(0, count);
    }

    createNewsCard(news) {
        const defaultImage = 'https://via.placeholder.com/300x200?text=新闻图片';
        const defaultBrief = '暂无简介';
        
        return `
            <article class="news-card">
                <img src="${news.imageUrl || defaultImage}" alt="${news.title}" />
                <div class="news-card-content">
                    <h3><a href="#" style="text-decoration: none;">${news.title}</a></h3>
                    <p>${news.brief || defaultBrief}</p>
                    <div class="news-card-meta">
                        <span>${this.formatDate(news.createTime)}</span>
                    </div>
                </div>
            </article>
        `;
    }

    createTrendingCard(news) {
        const defaultImage = 'https://via.placeholder.com/300x150?text=新闻图片';
        return `
            <article class="trending-card">
                <img src="${news.imageUrl || defaultImage}" alt="${news.title}" />
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
        // 广告内容配置
        const adContent = {
            imageUrl: 'https://28406602.s21i.faimallusr.com/2/ABUIABACGAAgo4qkqQYogNOnvwE!800x800.jpg',
            title: '金窖美酒1988',
            link: 'http://www.zgxyhpt.com/sys-pd/1359.html',
            price: '￥1199元'
        };

        return `
            <article class="news-card ad-card">
                <div class="ad-label">广告</div>
                <img src="${adContent.imageUrl}" alt="${adContent.title}" />
                <div class="news-card-content">
                    <h3><a href="${adContent.link}" target="_blank" rel="noopener noreferrer">${adContent.title}</a></h3>
                    <div class="price-tag">${adContent.price}</div>
                </div>
            </article>
        `;
    }

    formatDate(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toLocaleDateString('zh-CN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    refreshNews() {
        const randomNews = this.getRandomNews(7);
        const cards = [...randomNews.map(news => this.createNewsCard(news)), this.createAdCard()];
        cards.sort(() => Math.random() - 0.5);
        this.newsContainer.innerHTML = cards.join('');
    }

    updateTrending() {
        const trendingNews = this.getRandomNews(3);
        this.trendingContainer.innerHTML = trendingNews
            .map(news => this.createTrendingCard(news))
            .join('');
    }
}

new NewsManager(); 