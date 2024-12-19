# 第三阶段：广告模块实现

## 1. 模块结构

```
src/main/java/com/adplatform/module/ad/
├── controller/
│   ├── AdvertisementController.java
│   └── MaterialController.java
├── service/
│   ├── AdvertisementService.java
│   ├── AdvertisementServiceImpl.java
│   ├── MaterialService.java
│   └── MaterialServiceImpl.java
├── repository/
│   ├── AdvertisementRepository.java
│   └── MaterialRepository.java
├── entity/
│   ├── Advertisement.java
│   └── Material.java
├── dto/
│   ├── AdvertisementDTO.java
│   ├── MaterialDTO.java
│   ├── AdCreateRequest.java
│   └── AdUpdateRequest.java
└── enums/
    ├── AdStatus.java
    └── AdType.java
```

## 2. 核心代码实现

### 2.1 广告实体类
```java
@Data
@Entity
@Table(name = "advertisement")
public class Advertisement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String description;
    private Long userId;
    private Integer type;
    private Integer status;
    private BigDecimal budget;
    private BigDecimal dailyBudget;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    @OneToMany(mappedBy = "advertisement", cascade = CascadeType.ALL)
    private List<Material> materials;
}
```

### 2.2 广告服务实现
```java
@Service
@Transactional
public class AdvertisementServiceImpl implements AdvertisementService {
    @Autowired
    private AdvertisementRepository advertisementRepository;
    
    @Autowired
    private MaterialService materialService;
    
    @Override
    public AdvertisementDTO create(AdCreateRequest request) {
        // 验证预算
        validateBudget(request.getBudget(), request.getDailyBudget());
        
        // 创建广告
        Advertisement ad = new Advertisement();
        BeanUtils.copyProperties(request, ad);
        ad.setStatus(AdStatus.DRAFT.getCode());
        ad.setCreateTime(LocalDateTime.now());
        ad.setUpdateTime(LocalDateTime.now());
        
        // 保存广告
        ad = advertisementRepository.save(ad);
        
        // 处理素材
        if (request.getMaterials() != null) {
            materialService.saveMaterials(ad.getId(), request.getMaterials());
        }
        
        return convertToDTO(ad);
    }
    
    @Override
    public AdvertisementDTO submit(Long id) {
        Advertisement ad = getAdvertisement(id);
        
        // 验证广告状态
        if (ad.getStatus() != AdStatus.DRAFT.getCode()) {
            throw new ApiException("只有草稿状态的广告可以提交审核");
        }
        
        // 更新状态
        ad.setStatus(AdStatus.PENDING_REVIEW.getCode());
        ad.setUpdateTime(LocalDateTime.now());
        
        return convertToDTO(advertisementRepository.save(ad));
    }
    
    @Override
    public AdvertisementDTO approve(Long id) {
        Advertisement ad = getAdvertisement(id);
        
        // 验证广告状态
        if (ad.getStatus() != AdStatus.PENDING_REVIEW.getCode()) {
            throw new ApiException("只有待审核状态的广告可以审核通过");
        }
        
        // 更新状态
        ad.setStatus(AdStatus.APPROVED.getCode());
        ad.setUpdateTime(LocalDateTime.now());
        
        return convertToDTO(advertisementRepository.save(ad));
    }
}
```

### 2.3 素材上传服务
```java
@Service
public class MaterialServiceImpl implements MaterialService {
    @Autowired
    private MaterialRepository materialRepository;
    
    @Autowired
    private OSSClient ossClient;
    
    @Override
    public MaterialDTO uploadMaterial(MultipartFile file, Integer type) {
        // 验证文件
        validateFile(file);
        
        // 上传到OSS
        String url = uploadToOSS(file);
        
        // 保存素材信息
        Material material = new Material();
        material.setType(type);
        material.setUrl(url);
        material.setSize(file.getSize());
        material.setCreateTime(LocalDateTime.now());
        
        return convertToDTO(materialRepository.save(material));
    }
    
    private String uploadToOSS(MultipartFile file) {
        String fileName = generateFileName(file);
        try {
            ossClient.putObject(
                "ad-platform",
                fileName,
                file.getInputStream()
            );
            return ossClient.generatePresignedUrl(
                "ad-platform",
                fileName,
                DateUtils.addDays(new Date(), 7)
            ).toString();
        } catch (IOException e) {
            throw new ApiException("文件上传失败", e);
        }
    }
}
```

## 3. 接口说明

### 3.1 广告接口
- POST `/api/v1/ads`: 创建广告
- PUT `/api/v1/ads/{id}`: 更新广告
- GET `/api/v1/ads/{id}`: 获取广告详情
- GET `/api/v1/ads`: 获取广告列表
- POST `/api/v1/ads/{id}/submit`: 提交审核
- POST `/api/v1/ads/{id}/approve`: 审核通过
- POST `/api/v1/ads/{id}/reject`: 审核拒绝
- POST `/api/v1/ads/{id}/start`: 开始投放
- POST `/api/v1/ads/{id}/pause`: 暂停投放

### 3.2 素材接口
- POST `/api/v1/materials`: 上传素材
- GET `/api/v1/materials/{id}`: 获取素材
- DELETE `/api/v1/materials/{id}`: 删除素材

## 4. 状态流转

### 4.1 广告状态流转
```
草稿(DRAFT) 
  -> 待审核(PENDING_REVIEW) 
  -> 审核中(REVIEWING) 
  -> 已通过(APPROVED)/已拒绝(REJECTED)
  -> 投放中(RUNNING)
  -> 已暂停(PAUSED)
  -> 已完成(COMPLETED)
```

### 4.2 状态流转规则
1. 只有草稿状态可以编辑
2. 只有草稿状态可以提交审核
3. 只有待审核状态可以审核
4. 只有已通过状态可以开始投放
5. 只有投放中状态可以暂停
6. 已完成状态为终态

## 5. 测试用例

### 5.1 创建广告测试
```java
@Test
public void testCreateAd() {
    AdCreateRequest request = new AdCreateRequest();
    request.setTitle("测试广告");
    request.setType(AdType.IMAGE.getCode());
    request.setBudget(new BigDecimal("1000"));
    request.setDailyBudget(new BigDecimal("100"));
    request.setStartTime(LocalDateTime.now().plusDays(1));
    request.setEndTime(LocalDateTime.now().plusDays(10));
    
    AdvertisementDTO result = adService.create(request);
    assertNotNull(result);
    assertEquals("测试广告", result.getTitle());
    assertEquals(AdStatus.DRAFT.getCode(), result.getStatus());
}
```

### 5.2 审核流程测试
```java
@Test
public void testAdReviewProcess() {
    // 提交审核
    AdvertisementDTO ad = adService.submit(1L);
    assertEquals(AdStatus.PENDING_REVIEW.getCode(), ad.getStatus());
    
    // 审核通过
    ad = adService.approve(1L);
    assertEquals(AdStatus.APPROVED.getCode(), ad.getStatus());
    
    // 开始投放
    ad = adService.start(1L);
    assertEquals(AdStatus.RUNNING.getCode(), ad.getStatus());
}
``` 