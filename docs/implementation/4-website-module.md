# 第四阶段：网站模块实现

## 1. 模块结构

```
src/main/java/com/adplatform/module/website/
├── controller/
│   ├── WebsiteController.java
│   └── AdSpaceController.java
├── service/
│   ├── WebsiteService.java
│   ├── WebsiteServiceImpl.java
│   ├── AdSpaceService.java
│   └── AdSpaceServiceImpl.java
├── repository/
│   ├── WebsiteRepository.java
│   └── AdSpaceRepository.java
├── entity/
│   ├── Website.java
│   └── AdSpace.java
├── dto/
│   ├── WebsiteDTO.java
│   ├── AdSpaceDTO.java
│   ├── WebsiteCreateRequest.java
│   └── AdSpaceCreateRequest.java
└── generator/
    └── AdCodeGenerator.java
```

## 2. 核心代码实现

### 2.1 网站实体类
```java
@Data
@Entity
@Table(name = "website")
public class Website {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;
    private String name;
    private String domain;
    private String description;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    @OneToMany(mappedBy = "website", cascade = CascadeType.ALL)
    private List<AdSpace> adSpaces;
}
```

### 2.2 广告位实体类
```java
@Data
@Entity
@Table(name = "ad_space")
public class AdSpace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long websiteId;
    private String name;
    private Integer type;
    private Integer width;
    private Integer height;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    @ManyToOne
    @JoinColumn(name = "website_id", insertable = false, updatable = false)
    private Website website;
}
```

### 2.3 网站服务实现
```java
@Service
@Transactional
public class WebsiteServiceImpl implements WebsiteService {
    @Autowired
    private WebsiteRepository websiteRepository;
    
    @Override
    public WebsiteDTO create(WebsiteCreateRequest request) {
        // 验证域名唯一性
        if (websiteRepository.existsByDomain(request.getDomain())) {
            throw new ApiException("域名已存在");
        }
        
        // 创建网站
        Website website = new Website();
        BeanUtils.copyProperties(request, website);
        website.setStatus(WebsiteStatus.PENDING.getCode());
        website.setCreateTime(LocalDateTime.now());
        website.setUpdateTime(LocalDateTime.now());
        
        return convertToDTO(websiteRepository.save(website));
    }
    
    @Override
    public WebsiteDTO approve(Long id) {
        Website website = getWebsite(id);
        
        // 验证状态
        if (website.getStatus() != WebsiteStatus.PENDING.getCode()) {
            throw new ApiException("只有待审核状态的网站可以审核");
        }
        
        // 更新状态
        website.setStatus(WebsiteStatus.NORMAL.getCode());
        website.setUpdateTime(LocalDateTime.now());
        
        return convertToDTO(websiteRepository.save(website));
    }
}
```

### 2.4 广告代码生成器
```java
@Component
public class AdCodeGenerator {
    private static final String SCRIPT_TEMPLATE = """
        <script src="//cdn.adplatform.com/ad.js"></script>
        <div id="ad-%d" class="ad-space"></div>
        <script>
            new AdLoader({
                spaceId: %d,
                container: 'ad-%d',
                width: %d,
                height: %d
            }).load();
        </script>
    """;
    
    public String generateCode(AdSpace adSpace) {
        return String.format(
            SCRIPT_TEMPLATE,
            adSpace.getId(),
            adSpace.getId(),
            adSpace.getId(),
            adSpace.getWidth(),
            adSpace.getHeight()
        );
    }
}
```

## 3. 接口说明

### 3.1 网站接口
- POST `/api/v1/websites`: 创建网站
- PUT `/api/v1/websites/{id}`: 更新网站信息
- GET `/api/v1/websites/{id}`: 获取网站详情
- GET `/api/v1/websites`: 获取网站列表
- POST `/api/v1/websites/{id}/approve`: 审核通过
- POST `/api/v1/websites/{id}/reject`: 审核拒绝

### 3.2 广告位接口
- POST `/api/v1/websites/{websiteId}/spaces`: 创建广告位
- PUT `/api/v1/spaces/{id}`: 更新广告位
- GET `/api/v1/spaces/{id}`: 获取广告位详情
- GET `/api/v1/websites/{websiteId}/spaces`: 获取网站的广告位列表
- GET `/api/v1/spaces/{id}/code`: 获取广告代码

## 4. 广告代码集成

### 4.1 JavaScript SDK
```javascript
// ad.js
class AdLoader {
    constructor(options) {
        this.options = options;
        this.container = document.getElementById(options.container);
    }
    
    async load() {
        try {
            // 请求广告数据
            const ad = await this.fetchAd();
            
            // 渲染广告
            this.render(ad);
            
            // 发送展示记录
            this.sendImpression(ad);
            
            // 绑定点击事件
            this.bindClickEvent(ad);
        } catch (error) {
            console.error('加载广告失败', error);
        }
    }
    
    async fetchAd() {
        const response = await fetch(
            `//api.adplatform.com/api/v1/spaces/${this.options.spaceId}/ad`
        );
        return response.json();
    }
    
    render(ad) {
        if (ad.type === 'IMAGE') {
            this.renderImage(ad);
        } else if (ad.type === 'VIDEO') {
            this.renderVideo(ad);
        } else {
            this.renderText(ad);
        }
    }
    
    sendImpression(ad) {
        fetch('//api.adplatform.com/api/v1/stats/impression', {
            method: 'POST',
            body: JSON.stringify({
                adId: ad.id,
                spaceId: this.options.spaceId
            })
        });
    }
}
```

### 4.2 集成示例
```html
<!-- 网站页面 -->
<div class="sidebar">
    <!-- 广告位 -->
    <script src="//cdn.adplatform.com/ad.js"></script>
    <div id="ad-123" class="ad-space"></div>
    <script>
        new AdLoader({
            spaceId: 123,
            container: 'ad-123',
            width: 300,
            height: 250
        }).load();
    </script>
</div>
```

## 5. 测试用例

### 5.1 网站创建测试
```java
@Test
public void testCreateWebsite() {
    WebsiteCreateRequest request = new WebsiteCreateRequest();
    request.setName("测试网站");
    request.setDomain("test.com");
    request.setDescription("测试网站描述");
    
    WebsiteDTO result = websiteService.create(request);
    assertNotNull(result);
    assertEquals("test.com", result.getDomain());
    assertEquals(WebsiteStatus.PENDING.getCode(), result.getStatus());
}
```

### 5.2 广告位创建测试
```java
@Test
public void testCreateAdSpace() {
    AdSpaceCreateRequest request = new AdSpaceCreateRequest();
    request.setWebsiteId(1L);
    request.setName("侧边栏广告位");
    request.setType(AdSpaceType.SIDEBAR.getCode());
    request.setWidth(300);
    request.setHeight(250);
    
    AdSpaceDTO result = adSpaceService.create(request);
    assertNotNull(result);
    assertEquals(Integer.valueOf(300), result.getWidth());
    assertEquals(Integer.valueOf(250), result.getHeight());
}
``` 