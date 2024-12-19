# 第五阶段：统计模块实现

## 1. 模块结构

```
src/main/java/com/adplatform/module/stats/
├── controller/
│   ├── StatsController.java
│   └── ReportController.java
├── service/
│   ├── StatsService.java
│   ├── StatsServiceImpl.java
│   ├── ReportService.java
│   └── ReportServiceImpl.java
├── repository/
│   ├── AdImpressionRepository.java
│   ├── AdClickRepository.java
│   └── AdStatsRepository.java
├── entity/
│   ├── AdImpression.java
│   ├── AdClick.java
│   └── AdStats.java
├── dto/
│   ├── StatsDTO.java
│   ├── ReportDTO.java
│   └── StatsQueryRequest.java
└── task/
    └── DailyStatsTask.java
```

## 2. 核心代码实现

### 2.1 统计记录实体
```java
@Data
@Entity
@Table(name = "ad_stats_daily")
public class AdStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long adId;
    private LocalDate date;
    private Integer impressions;
    private Integer clicks;
    private BigDecimal cost;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    public void incrementImpressions() {
        this.impressions = this.impressions + 1;
    }
    
    public void incrementClicks() {
        this.clicks = this.clicks + 1;
    }
    
    public void addCost(BigDecimal cost) {
        this.cost = this.cost.add(cost);
    }
}
```

### 2.2 统计服务实现
```java
@Service
@Transactional
public class StatsServiceImpl implements StatsService {
    @Autowired
    private AdImpressionRepository impressionRepository;
    
    @Autowired
    private AdClickRepository clickRepository;
    
    @Autowired
    private AdStatsRepository statsRepository;
    
    @Override
    public void recordImpression(Long adId, Long adSpaceId, String ip, String userAgent) {
        // 记录展示
        AdImpression impression = new AdImpression();
        impression.setAdId(adId);
        impression.setAdSpaceId(adSpaceId);
        impression.setIp(ip);
        impression.setUserAgent(userAgent);
        impression.setCreateTime(LocalDateTime.now());
        
        impressionRepository.save(impression);
        
        // 更新统计
        updateStats(adId, StatsType.IMPRESSION);
    }
    
    @Override
    public void recordClick(Long adId, Long adSpaceId, String ip, String userAgent) {
        // 记录点击
        AdClick click = new AdClick();
        click.setAdId(adId);
        click.setAdSpaceId(adSpaceId);
        click.setIp(ip);
        click.setUserAgent(userAgent);
        click.setCreateTime(LocalDateTime.now());
        
        clickRepository.save(click);
        
        // 更新统计
        updateStats(adId, StatsType.CLICK);
    }
    
    private void updateStats(Long adId, StatsType type) {
        LocalDate today = LocalDate.now();
        AdStats stats = statsRepository.findByAdIdAndDate(adId, today)
            .orElseGet(() -> createNewStats(adId, today));
        
        if (type == StatsType.IMPRESSION) {
            stats.incrementImpressions();
        } else if (type == StatsType.CLICK) {
            stats.incrementClicks();
        }
        
        statsRepository.save(stats);
    }
}
```

### 2.3 定时统计任务
```java
@Component
@EnableScheduling
public class DailyStatsTask {
    @Autowired
    private AdStatsRepository statsRepository;
    
    @Autowired
    private AdImpressionRepository impressionRepository;
    
    @Autowired
    private AdClickRepository clickRepository;
    
    @Scheduled(cron = "0 5 0 * * ?") // 每天凌晨0:05执行
    public void generateDailyStats() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime startTime = yesterday.atStartOfDay();
        LocalDateTime endTime = yesterday.plusDays(1).atStartOfDay();
        
        // 统计每个广告的数据
        List<Object[]> stats = impressionRepository.countByAdIdAndDateRange(
            startTime, endTime);
            
        for (Object[] stat : stats) {
            Long adId = (Long) stat[0];
            Long impressions = (Long) stat[1];
            Long clicks = (Long) stat[2];
            BigDecimal cost = (BigDecimal) stat[3];
            
            // 保存统计数据
            AdStats adStats = new AdStats();
            adStats.setAdId(adId);
            adStats.setDate(yesterday);
            adStats.setImpressions(impressions.intValue());
            adStats.setClicks(clicks.intValue());
            adStats.setCost(cost);
            adStats.setCreateTime(LocalDateTime.now());
            adStats.setUpdateTime(LocalDateTime.now());
            
            statsRepository.save(adStats);
        }
    }
}
```

## 3. 接口说明

### 3.1 统计接口
- POST `/api/v1/stats/impression`: 记录广告展示
- POST `/api/v1/stats/click`: 记录广告点击
- GET `/api/v1/stats/ad/{id}`: 获取广告统计数据
- GET `/api/v1/stats/website/{id}`: 获取网站统计数据

### 3.2 报表接口
- GET `/api/v1/reports/daily`: 获取每日报表
- GET `/api/v1/reports/weekly`: 获取每周报表
- GET `/api/v1/reports/monthly`: 获取每月报表
- POST `/api/v1/reports/export`: 导出报表

## 4. 数据分析

### 4.1 关键指标
1. 展示量（Impressions）
2. 点击量（Clicks）
3. 点击率（CTR）
4. 消费金额（Cost）
5. 千次展示成本（CPM）
6. 点击成本（CPC）

### 4.2 分析维度
1. 时间维度：小时、天、周、月
2. 广告维度：广告ID、广告主
3. 网站维度：网站ID、广告位
4. 地域维度：IP、地区

## 5. 测试用例

### 5.1 统计记录测试
```java
@Test
public void testRecordStats() {
    // 记录展示
    statsService.recordImpression(1L, 1L, "127.0.0.1", "Mozilla/5.0");
    
    // 记录点击
    statsService.recordClick(1L, 1L, "127.0.0.1", "Mozilla/5.0");
    
    // 验证统计
    AdStats stats = statsRepository.findByAdIdAndDate(1L, LocalDate.now());
    assertNotNull(stats);
    assertEquals(Integer.valueOf(1), stats.getImpressions());
    assertEquals(Integer.valueOf(1), stats.getClicks());
}
```

### 5.2 报表生成测试
```java
@Test
public void testGenerateReport() {
    LocalDate startDate = LocalDate.now().minusDays(7);
    LocalDate endDate = LocalDate.now();
    
    ReportDTO report = reportService.generateReport(startDate, endDate);
    assertNotNull(report);
    assertFalse(report.getItems().isEmpty());
}
``` 