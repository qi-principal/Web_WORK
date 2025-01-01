class EntertainmentNewsManager {
    constructor() {
        this.container = document.getElementById('entertainmentNewsContainer');
        this.refreshBtn = document.getElementById('refreshBtn');
        this.newsData = [];
        this.init();
    }

    async init() {
        console.log('初始化EntertainmentNewsManager...');
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
            console.log('开始获取文娱新闻...');
<<<<<<< HEAD
            const response = await fetch('https://10142zp2193.goho.co/api/news/entertainment');
=======
            const response = await fetch('http://localhost:8080/api/news/entertainment');
>>>>>>> origin/news
            console.log('文娱新闻API响应:', response);
            const result = await response.json();
            console.log('获取到的文娱新闻数据:', result);
            
            if (result.code === 1 && Array.isArray(result.data)) {
                this.newsData = result.data;
                console.log('成功获取文娱新闻，数量:', this.newsData.length);
                this.refreshNews();
            } else {
                console.error('获取文娱新闻失败:', result.msg);
            }
        } catch (error) {
            console.error('获取文娱新闻失败:', error);
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
        console.log('创建文娱新闻卡片:', news);
        const defaultImage = 'https://via.placeholder.com/300x200?text=新闻图片';
        
        return `
            <article class="news-card">
                <img src="${news.imageUrl || defaultImage}" alt="${news.title}" onerror="this.src='${defaultImage}'"/>
                <div class="news-card-content">
                    <h3><a href="#" style="text-decoration: none;">${news.title}</a></h3>
                    <p>${news.brief || '暂无简介'}</p>
                    <div class="news-card-meta">
                        <span class="date">${this.formatDate(news.createTime)}</span>
                        <span class="category">${news.category || '文娱'}</span>
                    </div>
                </div>
            </article>
        `;
    }

    createAdCard() {
        console.log('创建广告卡片');
<<<<<<< HEAD
        return `
            <article class="news-card ad-card">
                <div class="ad-label">广告</div>
                <iframe 
                    src="http://192.168.10.155:8080/#/ad-preview/12" 
                    style="width: 100%; height: 300px; border: none; overflow: hidden;"
                    loading="lazy"
                    title="广告内容"
                ></iframe>
=======
        const adContent = {
            imageUrl: 'https://28406602.s21i.faimallusr.com/2/ABUIABACGAAgo4qkqQYogNOnvwE!800x800.jpg',
            title: '金窖美酒1988',
            link: 'http://www.zgxyhpt.com/sys-pd/1359.html',
            price: '￥1199元'
        };
        
        return `
            <article class="news-card ad-card">
                <div class="ad-label">广告</div>
                <img src="${adContent.imageUrl}" alt="${adContent.title}" onerror="this.src='https://via.placeholder.com/300x200?text=广告图片'"/>
                <div class="news-card-content">
                    <h3><a href="${adContent.link}" target="_blank" rel="noopener noreferrer">${adContent.title}</a></h3>
                    <div class="price-tag">${adContent.price}</div>
                </div>
>>>>>>> origin/news
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
        console.log('开始刷新文娱新闻...');
        
        if (!this.container) {
            console.error('未找到文娱新闻容器元素!');
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
        console.log('文娱新闻刷新完成');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    console.log('文娱新闻页面DOM加载完成');
    new EntertainmentNewsManager();
});

setInterval(() => {
    console.log('执行定时刷新');
    document.getElementById('refreshBtn').click();
}, 300000); 