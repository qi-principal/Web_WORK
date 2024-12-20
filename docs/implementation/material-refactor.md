# 广告素材模块重构说明文档

## 1. 项目现状

### 1.1 当前架构
```
src/
└── main/
    └── java/
        └── com/adplatform/
            └── module/
                └── ad/
                    ├── controller/
                    │   └── MaterialController.java
                    ├── service/
                    │   ├── MaterialService.java
                    │   └── impl/
                    │       └── MaterialServiceImpl.java
                    ├── entity/
                    │   └── Material.java
                    ├── dto/
                    │   └── MaterialDTO.java
                    └── mapper/
                        └── MaterialMapper.java
```

### 1.2 现有数据库设计
```sql
CREATE TABLE `ad_material` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `ad_id` bigint,
    `type` int NOT NULL,
    `content` varchar(255),
    `url` varchar(255) NOT NULL,
    `size` bigint NOT NULL,
    `create_time` datetime NOT NULL,
    PRIMARY KEY (`id`)
);
```

## 2. 重构目标

### 2.1 主要问题
1. 素材与广告强耦合，一个素材只能属于一个广告
2. 素材无法复用，相同素材需要重复上传
3. 素材生命周期与广告完全绑定
4. 素材管理和统计较为困难

### 2.2 改进目标
1. 实现素材与广告的多对多关系
2. 支持素材复用
3. 优化素材生命周期管理
4. 提升系统扩展性

## 3. 重构方案

### 3.1 数据库变更
1. 修改素材表结构，移除`ad_id`字段
2. 新增广告素材关联表

```sql
-- 修改素材表
ALTER TABLE `ad_material` DROP COLUMN `ad_id`;

-- 创建广告素材关联表
CREATE TABLE `ad_material_relation` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `ad_id` bigint NOT NULL,
    `material_id` bigint NOT NULL,
    `create_time` datetime NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_ad_material` (`ad_id`, `material_id`),
    KEY `idx_ad_id` (`ad_id`),
    KEY `idx_material_id` (`material_id`)
);
```

### 3.2 代码变更

#### 3.2.1 新增实体类
```java
@Data
@TableName("ad_material_relation")
public class AdMaterialRelation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long adId;
    private Long materialId;
    private LocalDateTime createTime;
}
```

#### 3.2.2 修改Material实体
```java
@Data
@TableName("ad_material")
public class Material {
    // 移除 adId 字段
    // 保持其他字段不变
}
```

#### 3.2.3 新增Mapper接口
```java
@Mapper
public interface AdMaterialRelationMapper extends BaseMapper<AdMaterialRelation> {
    List<Material> selectMaterialsByAdId(Long adId);
    List<Long> selectAdIdsByMaterialId(Long materialId);
}
```

#### 3.2.4 服务层变更
- MaterialService接口新增方法：
  - addMaterialToAd
  - removeMaterialFromAd
  - listAdsByMaterialId
- 修改现有方法实现，通过关联表查询

### 3.3 接口变更
新增接口：
- POST `/api/v1/materials/{materialId}/ads/{adId}` - 添加素材到广告
- DELETE `/api/v1/materials/{materialId}/ads/{adId}` - 移除广告素材关联
- GET `/api/v1/materials/{materialId}/ads` - 获取使用该素材的广告列表

## 4. 迁移方案

### 4.1 数据迁移步骤
1. 备份现有数据
2. 创建新的关联表
3. 将现有素材关联关系迁移到新表
4. 移除素材表中的`ad_id`字段

```sql
-- 迁移数据
INSERT INTO ad_material_relation (ad_id, material_id, create_time)
SELECT ad_id, id, create_time
FROM ad_material
WHERE ad_id IS NOT NULL;
```

### 4.2 代码迁移步骤
1. 新增关联表相关代码
2. 修改现有接口实现
3. 添加新接口
4. 进行完整测试
5. 灰度发布

## 5. 测试计划

### 5.1 单元测试
- 素材基础CRUD测试
- 广告素材关联关系测试
- 素材复用场景测试

### 5.2 集成测试
- 数据迁移测试
- 接口兼容性测试
- 性能测试

### 5.3 回归测试
- 现有功能回归
- 广告投放流程测试
- 素材管理流程测试

## 6. 风险评估

### 6.1 潜在风险
1. 数据迁移过程中的数据一致性风险
2. 接口变更对现有业务的影响
3. 性能影响评估

### 6.2 规避措施
1. 完整的数据备份
2. 详细的回滚方案
3. 灰度发布策略
4. 完整的监控指标

## 7. 时间规划

### 7.1 开发阶段（5个工作日）
- Day 1: 数据库设计和实体类修改
- Day 2: 服务层代码开发
- Day 3: 接口开发和单元测试
- Day 4: 集成测试
- Day 5: 文档完善和代码审查

### 7.2 测试阶段（3个工作日）
- Day 1: 功能测试
- Day 2: 性能测试
- Day 3: 回归测试

### 7.3 部署阶段（2个工作日）
- Day 1: 数据迁移和灰度发布
- Day 2: 生产环境监控和问题修复

## 8. 后续优化

### 8.1 功能优化
1. 素材分组管理
2. 素材使用统计
3. 素材智能推荐

### 8.2 性能优化
1. 素材缓存优化
2. 查询性能优化
3. 存储优化 