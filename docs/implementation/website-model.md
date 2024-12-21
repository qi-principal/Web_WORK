## Website 模块设计说明

### 1. 功能概述

**Website 模块** 主要为 **网站主用户** 提供以下功能：

- **网站管理**：
  - 创建网站
  - 更新网站信息
  - 获取网站详情
  - 获取网站列表
  - 审核网站（管理员权限）
- **广告位管理**：
  - 创建广告位
  - 更新广告位信息
  - 获取广告位详情
  - 获取网站的广告位列表
  - 获取广告位嵌入代码（iframe 的 src）
  - 审核广告位（管理员权限）

### 2. 数据库设计

#### 2.1 表结构

##### 1. website 表：管理网站信息

**业务描述**：

- 一个用户可以拥有多个网站：每个网站通过 `user_id` 与用户关联，确保网站与其创建者之间的关系。
- 网站的状态管理：`status` 字段表示网站的审核状态。常见的状态包括“待审核”（0）、“已审核”（1）、“已拒绝”（2）。
- 网站与广告位关联：每个网站可能有多个广告位。通过 `website_id` 外键关联到 `ad_space` 表，实现网站与广告位的“一对多”关系。
- 创建和更新时间：`create_time` 和 `update_time` 字段分别记录网站的创建和最后更新时间。

**表结构**：

```sql
CREATE TABLE `website` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '网站ID',
    `user_id` bigint NOT NULL COMMENT '所属用户ID',
    `name` varchar(100) NOT NULL COMMENT '网站名称',
    `url` varchar(255) NOT NULL COMMENT '网站URL',
    `description` varchar(500) DEFAULT NULL COMMENT '网站描述',
    `status` int NOT NULL DEFAULT '0' COMMENT '状态：0-待审核、1-已审核、2-已拒绝',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_website_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网站表';
```

**字段说明**：

- `id`：网站的唯一标识符。
- `user_id`：与 `user` 表关联，表示该网站属于哪个用户。
- `name`：网站名称。
- `url`：网站URL地址。
- `description`：网站描述信息。
- `status`：网站的审核状态（待审核、已审核、已拒绝）。
- `create_time`：网站创建时间。
- `update_time`：网站最后更新时间。

##### 2. ad_space 表：管理广告位信息

**业务描述**：

- 广告位的基本属性：每个广告位通过 `ad_space` 表管理，字段包括广告位的名称、尺寸（宽高）以及对应的广告代码（通常是HTML或JavaScript代码）。
- 广告位与网站的关联：通过 `website_id` 外键，广告位与具体的网站进行关联，每个广告位只能属于一个网站，一个网站可以有多个广告位。
- 广告位状态管理：`status` 字段管理广告位的状态（如“待审核”、“已激活”、“已禁用”等），帮助系统管理员有效地控制广告位的使用。

**表结构**：

```sql
CREATE TABLE `ad_space` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '广告位ID',
    `website_id` bigint NOT NULL COMMENT '所属网站ID',
    `name` varchar(100) NOT NULL COMMENT '广告位名称',
    `width` int NOT NULL COMMENT '广告位宽度',
    `height` int NOT NULL COMMENT '广告位高度',
    `code` varchar(500) NOT NULL COMMENT '广告位代码（iframe src）',
    `status` int NOT NULL DEFAULT '0' COMMENT '状态：0-待审核、1-已审核、2-已拒绝',
    `create_time` datetime NOT NULL COMMENT '广告位创建时间',
    `update_time` datetime NOT NULL COMMENT '广告位更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_ad_space_unique` (`website_id`, `name`),
    KEY `idx_website_id` (`website_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_ad_space_website` FOREIGN KEY (`website_id`) REFERENCES `website` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告位表';
```

**字段说明**：

- `id`：广告位的唯一标识符。
- `website_id`：外键，指向 `website` 表，表示该广告位属于哪个网站。
- `name`：广告位的名称。
- `width`、`height`：广告位的尺寸。
- `code`：用于嵌入广告的代码，通常是 iframe 或其他 HTML 代码。
- `status`：广告位的审核状态（待审核、已审核、已拒绝）。
- `create_time`：广告位创建时间。
- `update_time`：广告位最后更新时间。

##### 3. page 表：管理前端页面与广告位的绑定

**业务描述**：

- 广告位和页面的绑定：每个广告位都对应一个前端页面。页面表通过 `ad_space_id` 字段与广告位进行关联。页面表不仅记录页面的URL，还可以保存页面内容、状态等其他信息。
- 页面状态管理：每个页面可能有不同的状态（如“已发布”、“草稿”、“已删除”等），这有助于前端展示不同状态的页面内容，特别是在广告投放任务需要根据页面状态进行筛选时。

**表结构**：

```sql
CREATE TABLE `page` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '页面ID',
    `ad_space_id` bigint NOT NULL COMMENT '广告位ID',
    `url` varchar(255) NOT NULL COMMENT '页面URL',
    `content` text DEFAULT NULL COMMENT '页面内容',
    `status` int NOT NULL DEFAULT '0' COMMENT '页面状态：0-待审核、1-已审核、2-已拒绝',
    `create_time` datetime NOT NULL COMMENT '页面创建时间',
    `update_time` datetime NOT NULL COMMENT '页面更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_ad_space_id` (`ad_space_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_page_ad_space` FOREIGN KEY (`ad_space_id`) REFERENCES `ad_space` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='页面表';
```

**字段说明**：

- `id`：页面的唯一标识符。
- `ad_space_id`：外键，指向 `ad_space` 表，表示该页面属于哪个广告位。
- `url`：页面URL，用于指向广告位对应的前端页面。
- `content`：页面内容，可以包含广告展示的HTML代码。
- `status`：页面的状态（待审核、已审核、已拒绝）。
- `create_time`：页面创建时间。
- `update_time`：页面最后更新时间。

### 3. 后端实现

#### 3.1 模块结构

在现有项目结构中新增 **website** 模块，结构如下：

```
src/
└── main/
    └── java/
        └── com/adplatform/
            ├── module/
            │   ├── ad/
            │   ├── statistics/
            │   ├── user/
            │   └── website/
            │       ├── controller/
            │       │   ├── WebsiteController.java
            │       │   ├── AdSpaceController.java
            │       │   └── PageController.java
            │       ├── service/
            │       │   ├── WebsiteService.java
            │       │   ├── AdSpaceService.java
            │       │   ├── PageService.java
            │       │   └── impl/
            │       │       ├── WebsiteServiceImpl.java
            │       │       ├── AdSpaceServiceImpl.java
            │       │       └── PageServiceImpl.java
            │       ├── mapper/
            │       │   ├── WebsiteMapper.java
            │       │   ├── AdSpaceMapper.java
            │       │   └── PageMapper.java
            │       ├── entity/
            │       │   ├── Website.java
            │       │   ├── AdSpace.java
            │       │   └── Page.java
            │       ├── dto/
            │       ├── converter/
            │       └── enums/
            └── delivery/
```

#### 3.2 实体类

##### Website.java

```java
package com.adplatform.module.website.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("website")
public class Website {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String name;
    private String url;
    private String description;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
```

##### AdSpace.java

```java
package com.adplatform.module.website.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("ad_space")
public class AdSpace {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long websiteId;
    private String name;
    private Integer width;
    private Integer height;
    private String code;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
```

##### Page.java

```java
package com.adplatform.module.website.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("page")
public class Page {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long adSpaceId;
    private String url;
    private String content;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
```

### 

#### 3.3 Mapper 接口

##### WebsiteMapper.java

```java
package com.adplatform.module.website.mapper;

import com.adplatform.module.website.entity.Website;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WebsiteMapper extends BaseMapper<Website> {
}
```

##### AdSpaceMapper.java

```java
package com.adplatform.module.website.mapper;

import com.adplatform.module.website.entity.AdSpace;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdSpaceMapper extends BaseMapper<AdSpace> {
}
```

#####  PageMapper.java

```java
package com.adplatform.module.website.mapper;

import com.adplatform.module.website.entity.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PageMapper extends BaseMapper<Page> {
}
```

### 

#### 3.4 服务接口与实现

##### WebsiteService.java

```java
package com.adplatform.module.website.service;

import com.adplatform.module.website.entity.Website;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface WebsiteService extends IService<Website> {
    void createWebsite(Website website);
    void updateWebsite(Long id, Website website);
    Website getWebsiteById(Long id);
    List<Website> getWebsites(Long userId, Integer status, int page, int size);
    void approveWebsite(Long id);
    void rejectWebsite(Long id);
}
```

##### WebsiteServiceImpl.java

```java
package com.adplatform.module.website.service.impl;

import com.adplatform.module.website.entity.Website;
import com.adplatform.module.website.mapper.WebsiteMapper;
import com.adplatform.module.website.service.WebsiteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class WebsiteServiceImpl extends ServiceImpl<WebsiteMapper, Website> implements WebsiteService {

    @Override
    @Transactional
    public void createWebsite(Website website) {
        website.setStatus(0); // 待审核
        this.save(website);
    }

    @Override
    @Transactional
    public void updateWebsite(Long id, Website website) {
        Website existing = this.getById(id);
        if (existing != null) {
            // 权限校验（略，此处假设已在控制器层处理）
            existing.setName(website.getName());
            existing.setUrl(website.getUrl());
            existing.setDescription(website.getDescription());
            existing.setStatus(0); // 更新后重新审核
            this.updateById(existing);
        }
    }

    @Override
    public Website getWebsiteById(Long id) {
        return this.getById(id);
    }

    @Override
    public List<Website> getWebsites(Long userId, Integer status, int page, int size) {
        // 使用 MyBatis-Plus 的分页插件
        return this.lambdaQuery()
                .eq(userId != null, Website::getUserId, userId)
                .eq(status != null, Website::getStatus, status)
                .page(new Page<>(page, size))
                .getRecords();
    }

    @Override
    @Transactional
    public void approveWebsite(Long id) {
        Website website = this.getById(id);
        if (website != null) {
            website.setStatus(1); // 已审核
            this.updateById(website);
        }
    }

    @Override
    @Transactional
    public void rejectWebsite(Long id) {
        Website website = this.getById(id);
        if (website != null) {
            website.setStatus(2); // 已拒绝
            this.updateById(website);
        }
    }
}
```

##### AdSpaceService.java

```java
package com.adplatform.module.website.service;

import com.adplatform.module.website.entity.AdSpace;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface AdSpaceService extends IService<AdSpace> {
    void createAdSpace(Long websiteId, AdSpace adSpace);
    void updateAdSpace(Long id, AdSpace adSpace);
    AdSpace getAdSpaceById(Long id);
    List<AdSpace> getAdSpaces(Long websiteId, Integer status, int page, int size);
    void approveAdSpace(Long id);
    void rejectAdSpace(Long id);
    String generateAdCode(Long adSpaceId);
}
```

##### AdSpaceServiceImpl.java

```java
package com.adplatform.module.website.service.impl;

import com.adplatform.module.website.entity.AdSpace;
import com.adplatform.module.website.mapper.AdSpaceMapper;
import com.adplatform.module.website.service.AdSpaceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class AdSpaceServiceImpl extends ServiceImpl<AdSpaceMapper, AdSpace> implements AdSpaceService {

    @Override
    @Transactional
    public void createAdSpace(Long websiteId, AdSpace adSpace) {
        adSpace.setWebsiteId(websiteId);
        adSpace.setStatus(0); // 待审核
        adSpace.setCode(generateAdCode(adSpace.getId())); // 生成广告代码
        this.save(adSpace);
    }

    @Override
    @Transactional
    public void updateAdSpace(Long id, AdSpace adSpace) {
        AdSpace existing = this.getById(id);
        if (existing != null) {
            // 权限校验（略，此处假设已在控制器层处理）
            existing.setName(adSpace.getName());
            existing.setWidth(adSpace.getWidth());
            existing.setHeight(adSpace.getHeight());
            existing.setStatus(0); // 更新后重新审核
            this.updateById(existing);
        }
    }

    @Override
    public AdSpace getAdSpaceById(Long id) {
        return this.getById(id);
    }

    @Override
    public List<AdSpace> getAdSpaces(Long websiteId, Integer status, int page, int size) {
        return this.lambdaQuery()
                .eq(websiteId != null, AdSpace::getWebsiteId, websiteId)
                .eq(status != null, AdSpace::getStatus, status)
                .page(new Page<>(page, size))
                .getRecords();
    }

    @Override
    @Transactional
    public void approveAdSpace(Long id) {
        AdSpace adSpace = this.getById(id);
        if (adSpace != null) {
            adSpace.setStatus(1); // 已审核
            this.updateById(adSpace);
        }
    }

    @Override
    @Transactional
    public void rejectAdSpace(Long id) {
        AdSpace adSpace = this.getById(id);
        if (adSpace != null) {
            adSpace.setStatus(2); // 已拒绝
            this.updateById(adSpace);
        }
    }

    @Override
    public String generateAdCode(Long adSpaceId) {
        // 生成唯一的广告展示页面URL
        String uniquePath = UUID.randomUUID().toString();
        // 假设广告展示页面的基础URL为：http://yourdomain.com/ad/display/{uniquePath}
        String adDisplayUrl = "http://yourdomain.com/ad/display/" + uniquePath;
        return "<iframe src=\"" + adDisplayUrl + "\" width=\"300\" height=\"250\" frameborder=\"0\"></iframe>";
    }
}
```

##### **PageService.java**

```java
package com.adplatform.module.website.service;

import com.adplatform.module.website.entity.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface PageService extends IService<Page> {
    void createPage(Long adSpaceId, Page page);
    void updatePage(Long id, Page page);
    Page getPageById(Long id);
    List<Page> getPages(Long adSpaceId, Integer status, int page, int size);
    void approvePage(Long id);
    void rejectPage(Long id);
}
```

#####  **PageServiceImpl.java**

```java
package com.adplatform.module.website.service.impl;

import com.adplatform.module.website.entity.Page;
import com.adplatform.module.website.mapper.PageMapper;
import com.adplatform.module.website.service.PageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page as MPPage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class PageServiceImpl extends ServiceImpl<PageMapper, Page> implements PageService {

    @Override
    @Transactional
    public void createPage(Long adSpaceId, Page page) {
        page.setAdSpaceId(adSpaceId);
        page.setStatus(0); // 待审核
        this.save(page);
    }

    @Override
    @Transactional
    public void updatePage(Long id, Page page) {
        Page existing = this.getById(id);
        if (existing != null) {
            // 权限校验（假设已在控制器层处理）
            existing.setUrl(page.getUrl());
            existing.setContent(page.getContent());
            existing.setStatus(0); // 更新后重新审核
            this.updateById(existing);
        }
    }

    @Override
    public Page getPageById(Long id) {
        return this.getById(id);
    }

    @Override
    public List<Page> getPages(Long adSpaceId, Integer status, int page, int size) {
        MPPage<Page> pageResult = this.page(new MPPage<>(page, size),
                new LambdaQueryWrapper<Page>()
                        .eq(adSpaceId != null, Page::getAdSpaceId, adSpaceId)
                        .eq(status != null, Page::getStatus, status));
        return pageResult.getRecords();
    }

    @Override
    @Transactional
    public void approvePage(Long id) {
        Page page = this.getById(id);
        if (page != null) {
            page.setStatus(1); // 已审核
            this.updateById(page);
        }
    }

    @Override
    @Transactional
    public void rejectPage(Long id) {
        Page page = this.getById(id);
        if (page != null) {
            page.setStatus(2); // 已拒绝
            this.updateById(page);
        }
    }
}
```

### 

#### 3.5 控制器

##### WebsiteController.java

```java
package com.adplatform.module.website.controller;

import com.adplatform.module.website.entity.Website;
import com.adplatform.module.website.service.WebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/websites")
public class WebsiteController {

    @Autowired
    private WebsiteService websiteService;

    @PostMapping
    public ResponseEntity<?> createWebsite(@RequestBody Website website) {
        websiteService.createWebsite(website);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            Map.of(
                "message", "网站创建成功，待审核。",
                "websiteId", website.getId()
            )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateWebsite(@PathVariable Long id, @RequestBody Website website) {
        websiteService.updateWebsite(id, website);
        return ResponseEntity.ok(Map.of("message", "网站更新成功，待审核。"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWebsite(@PathVariable Long id) {
        Website website = websiteService.getWebsiteById(id);
        if (website == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", Map.of("code", "NOT_FOUND", "message", "网站不存在。"))
            );
        }
        return ResponseEntity.ok(website);
    }

    @GetMapping
    public ResponseEntity<?> getWebsites(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Website> websites = websiteService.getWebsites(userId, status, page, size);
        // 这里可以进一步封装分页信息
        return ResponseEntity.ok(
            Map.of(
                "currentPage", page,
                "pageSize", size,
                "items", websites
            )
        );
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveWebsite(@PathVariable Long id) {
        websiteService.approveWebsite(id);
        return ResponseEntity.ok(Map.of("message", "网站已审核通过。"));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectWebsite(@PathVariable Long id) {
        websiteService.rejectWebsite(id);
        return ResponseEntity.ok(Map.of("message", "网站已审核拒绝。"));
    }
}
```

##### AdSpaceController.java

```java
package com.adplatform.module.website.controller;

import com.adplatform.module.website.entity.AdSpace;
import com.adplatform.module.website.service.AdSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AdSpaceController {

    @Autowired
    private AdSpaceService adSpaceService;

    @PostMapping("/websites/{websiteId}/spaces")
    public ResponseEntity<?> createAdSpace(@PathVariable Long websiteId, @RequestBody AdSpace adSpace) {
        adSpaceService.createAdSpace(websiteId, adSpace);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            Map.of(
                "message", "广告位创建成功，待审核。",
                "adSpaceId", adSpace.getId(),
                "adCode", adSpace.getCode()
            )
        );
    }

    @PutMapping("/spaces/{id}")
    public ResponseEntity<?> updateAdSpace(@PathVariable Long id, @RequestBody AdSpace adSpace) {
        adSpaceService.updateAdSpace(id, adSpace);
        AdSpace updatedAdSpace = adSpaceService.getAdSpaceById(id);
        return ResponseEntity.ok(
            Map.of(
                "message", "广告位更新成功，待审核。",
                "adCode", updatedAdSpace.getCode()
            )
        );
    }

    @GetMapping("/spaces/{id}")
    public ResponseEntity<?> getAdSpace(@PathVariable Long id) {
        AdSpace adSpace = adSpaceService.getAdSpaceById(id);
        if (adSpace == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", Map.of("code", "NOT_FOUND", "message", "广告位不存在。"))
            );
        }
        return ResponseEntity.ok(adSpace);
    }

    @GetMapping("/websites/{websiteId}/spaces")
    public ResponseEntity<?> getAdSpaces(
            @PathVariable Long websiteId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<AdSpace> adSpaces = adSpaceService.getAdSpaces(websiteId, status, page, size);
        // 这里可以进一步封装分页信息
        return ResponseEntity.ok(
            Map.of(
                "currentPage", page,
                "pageSize", size,
                "items", adSpaces
            )
        );
    }

    @GetMapping("/spaces/{id}/code")
    public ResponseEntity<?> getAdCode(@PathVariable Long id) {
        AdSpace adSpace = adSpaceService.getAdSpaceById(id);
        if (adSpace == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", Map.of("code", "NOT_FOUND", "message", "广告位不存在。"))
            );
        }
        return ResponseEntity.ok(Map.of("adCode", adSpace.getCode()));
    }

    @PostMapping("/spaces/{id}/approve")
    public ResponseEntity<?> approveAdSpace(@PathVariable Long id) {
        adSpaceService.approveAdSpace(id);
        AdSpace adSpace = adSpaceService.getAdSpaceById(id);
        return ResponseEntity.ok(
            Map.of(
                "message", "广告位已审核通过。",
                "adCode", adSpace.getCode()
            )
        );
    }

    @PostMapping("/spaces/{id}/reject")
    public ResponseEntity<?> rejectAdSpace(@PathVariable Long id) {
        adSpaceService.rejectAdSpace(id);
        return ResponseEntity.ok(Map.of("message", "广告位已审核拒绝。"));
    }
}
```

#####  PageController.java

```java
package com.adplatform.module.website.controller;

import com.adplatform.module.website.entity.Page;
import com.adplatform.module.website.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pages")
public class PageController {

    @Autowired
    private PageService pageService;

    @PostMapping("/ad_spaces/{adSpaceId}")
    public ResponseEntity<?> createPage(@PathVariable Long adSpaceId, @RequestBody Page page) {
        pageService.createPage(adSpaceId, page);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            Map.of(
                "message", "页面创建成功，待审核。",
                "pageId", page.getId(),
                "pageUrl", page.getUrl()
            )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePage(@PathVariable Long id, @RequestBody Page page) {
        pageService.updatePage(id, page);
        Page updatedPage = pageService.getPageById(id);
        return ResponseEntity.ok(
            Map.of(
                "message", "页面更新成功，待审核。",
                "pageUrl", updatedPage.getUrl()
            )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPage(@PathVariable Long id) {
        Page page = pageService.getPageById(id);
        if (page == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", Map.of("code", "NOT_FOUND", "message", "页面不存在。"))
            );
        }
        return ResponseEntity.ok(page);
    }

    @GetMapping
    public ResponseEntity<?> getPages(
            @RequestParam(required = false) Long adSpaceId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Page> pages = pageService.getPages(adSpaceId, status, page, size);
        // 这里可以进一步封装分页信息
        return ResponseEntity.ok(
            Map.of(
                "currentPage", page,
                "pageSize", size,
                "items", pages
            )
        );
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approvePage(@PathVariable Long id) {
        pageService.approvePage(id);
        Page page = pageService.getPageById(id);
        return ResponseEntity.ok(
            Map.of(
                "message", "页面已审核通过。",
                "pageUrl", page.getUrl()
            )
        );
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectPage(@PathVariable Long id) {
        pageService.rejectPage(id);
        return ResponseEntity.ok(Map.of("message", "页面已审核拒绝。"));
    }
}
```





### 4. API 接口说明

#### 4.1 网站管理接口

##### 4.1.1 创建网站

- **接口路径**: `POST /api/v1/websites`

- **描述**: 网站主用户创建新的网站，提交待审核。

- **请求头**:

  - `Authorization: Bearer <token>`
  - `Content-Type: application/json`

- **请求体**:

  ```json
  {
      "name": "string",          // 网站名称，必填
      "url": "string",           // 网站URL，必填
      "description": "string"    // 网站描述，可选
  }
  ```

- **响应**:

  - **成功 (201 Created)**:

    ```json
    {
        "message": "网站创建成功，待审核。",
        "websiteId": 12345
    }
    ```

  - **失败**:

    - **400 Bad Request**: 请求参数错误
    - **401 Unauthorized**: 未授权访问
    - **500 Internal Server Error**: 服务器内部错误

##### 4.1.2 更新网站信息

- **接口路径**: `PUT /api/v1/websites/{id}`

- **描述**: 网站主用户更新已创建的网站信息，更新后网站状态重新置为待审核。

- **请求头**:

  - `Authorization: Bearer <token>`
  - `Content-Type: application/json`

- **路径参数**:

  - `id` (long): 网站ID，必填

- **请求体**:

  ```json
  {
      "name": "string",          // 网站名称，必填
      "url": "string",           // 网站URL，必填
      "description": "string"    // 网站描述，可选
  }
  ```

- **响应**:

  - **成功 (200 OK)**:

    ```json
    {
        "message": "网站更新成功，待审核。"
    }
    ```

  - **失败**:

    - **400 Bad Request**: 请求参数错误
    - **401 Unauthorized**: 未授权访问
    - **403 Forbidden**: 无权限修改该网站
    - **404 Not Found**: 网站不存在
    - **500 Internal Server Error**: 服务器内部错误

##### 4.1.3 获取网站详情

- **接口路径**: `GET /api/v1/websites/{id}`

- **描述**: 获取指定ID的网站详细信息。

- 请求头

  :

  - `Authorization: Bearer <token>`

- 路径参数

  :

  - `id` (long): 网站ID，必填

- 响应

  :

  - **成功 (200 OK)**:

    ```json
    {
        "id": 12345,
        "userId": 67890,
        "name": "string",
        "url": "string",
        "description": "string",
        "status": 0,             // 状态：0-待审核、1-已审核、2-已拒绝
        "createTime": "2024-04-27T12:34:56Z",
        "updateTime": "2024-04-27T12:34:56Z"
    }
    ```

  - **失败**:

    - **401 Unauthorized**: 未授权访问
    - **403 Forbidden**: 无权限查看该网站
    - **404 Not Found**: 网站不存在
    - **500 Internal Server Error**: 服务器内部错误

##### 4.1.4 获取网站列表

- **接口路径**: `GET /api/v1/websites`

- **描述**: 获取网站主用户的所有网站列表，支持分页和筛选。

- 请求头

  :

  - `Authorization: Bearer <token>`

- 查询参数

  :

  - `userId` (long): 用户ID，选填（若为空，默认获取当前登录用户的ID）
  - `status` (int): 网站状态，选填（0-待审核、1-已审核、2-已拒绝）
  - `page` (int): 页码，默认值为1
  - `size` (int): 每页数量，默认值为10

- 响应

  :

  - **成功 (200 OK)**:

    ```json
    {
        "currentPage": 1,
        "pageSize": 10,
        "totalPages": 5,
        "totalItems": 50,
        "items": [
            {
                "id": 12345,
                "userId": 67890,
                "name": "string",
                "url": "string",
                "description": "string",
                "status": 0,
                "createTime": "2024-04-27T12:34:56Z",
                "updateTime": "2024-04-27T12:34:56Z"
            },
            // 更多网站
        ]
    }
    ```

  - **失败**:

    - **401 Unauthorized**: 未授权访问
    - **500 Internal Server Error**: 服务器内部错误

##### 4.1.5 审核网站

###### 4.1.5.1 审核通过

- **接口路径**: `POST /api/v1/websites/{id}/approve`

- **描述**: 管理员审核通过指定ID的网站。

- 请求头

  :

  - `Authorization: Bearer <token>`
  - **角色要求**: 管理员

- 路径参数

  :

  - `id` (long): 网站ID，必填

- 响应

  :

  - **成功 (200 OK)**:

    ```json
    {
        "message": "网站已审核通过。"
    }
    ```

  - **失败**:

    - **401 Unauthorized**: 未授权访问
    - **403 Forbidden**: 无权限审核该网站
    - **404 Not Found**: 网站不存在
    - **500 Internal Server Error**: 服务器内部错误

###### 4.1.5.2 审核拒绝

- **接口路径**: `POST /api/v1/websites/{id}/reject`

- **描述**: 管理员审核拒绝指定ID的网站。

- 请求头

  :

  - `Authorization: Bearer <token>`
  - **角色要求**: 管理员

- 路径参数

  :

  - `id` (long): 网站ID，必填

- 响应

  :

  - **成功 (200 OK)**:

    ```json
    {
        "message": "网站已审核拒绝。"
    }
    ```

  - **失败**:

    - **401 Unauthorized**: 未授权访问
    - **403 Forbidden**: 无权限审核该网站
    - **404 Not Found**: 网站不存在
    - **500 Internal Server Error**: 服务器内部错误

#### 4.2 广告位管理接口

##### 4.2.1 创建广告位

- **接口路径**: `POST /api/v1/websites/{websiteId}/spaces`

- **描述**: 网站主用户在指定网站下创建新的广告位，提交待审核。

- **请求头**:

  - `Authorization: Bearer <token>`
  - `Content-Type: application/json`

- **路径参数**:

  - `websiteId` (long): 网站ID，必填

- **请求体**:

  ```json
  {
      "name": "string",      // 广告位名称，必填
      "width": 300,          // 广告位宽度，必填
      "height": 250          // 广告位高度，必填
  }
  ```

- **响应**:

  - **成功 (201 Created)**:

    ```json
    {
        "message": "广告位创建成功，待审核。",
        "adSpaceId": 54321,
        "adCode": "<iframe src=\"http://yourdomain.com/ad/display/{uniquePath}\" width=\"300\" height=\"250\" frameborder=\"0\"></iframe>"
    }
    ```

  - **失败**:

    - **400 Bad Request**: 请求参数错误
    - **401 Unauthorized**: 未授权访问
    - **403 Forbidden**: 无权限创建广告位
    - **404 Not Found**: 指定网站不存在
    - **500 Internal Server Error**: 服务器内部错误

##### 4.2.2 更新广告位信息

- **接口路径**: `PUT /api/v1/spaces/{id}`

- **描述**: 网站主用户更新指定广告位的信息，更新后广告位状态重新置为待审核。

- **请求头**:

  - `Authorization: Bearer <token>`
  - `Content-Type: application/json`

- **路径参数**:

  - `id` (long): 广告位ID，必填

- **请求体**:

  ```json
  {
      "name": "string",      // 广告位名称，必填
      "width": 300,          // 广告位宽度，必填
      "height": 250          // 广告位高度，必填
  }
  ```

- **响应**:

  - **成功 (200 OK)**:

    ```json
    {
        "message": "广告位更新成功，待审核。",
        "adCode": "<iframe src=\"http://yourdomain.com/ad/display/{uniquePath}\" width=\"300\" height=\"250\" frameborder=\"0\"></iframe>"
    }
    ```

  - **失败**:

    - **400 Bad Request**: 请求参数错误
    - **401 Unauthorized**: 未授权访问
    - **403 Forbidden**: 无权限修改该广告位
    - **404 Not Found**: 广告位不存在
    - **500 Internal Server Error**: 服务器内部错误

##### 4.2.3 获取广告位详情

- **接口路径**: `GET /api/v1/spaces/{id}`

- **描述**: 获取指定ID的广告位详细信息。

- 请求头

  :

  - `Authorization: Bearer <token>`

- 路径参数

  :

  - `id` (long): 广告位ID，必填

- 响应

  :

  - **成功 (200 OK)**:

    ```json
    {
        "id": 54321,
        "websiteId": 12345,
        "name": "string",
        "width": 300,
        "height": 250,
        "code": "<iframe src=\"http://yourdomain.com/ad/display/{uniquePath}\" width=\"300\" height=\"250\" frameborder=\"0\"></iframe>",
        "status": 0,               // 状态：0-待审核、1-已审核、2-已拒绝
        "createTime": "2024-04-27T12:34:56Z",
        "updateTime": "2024-04-27T12:34:56Z"
    }
    ```

  - **失败**:

    - **401 Unauthorized**: 未授权访问
    - **403 Forbidden**: 无权限查看该广告位
    - **404 Not Found**: 广告位不存在
    - **500 Internal Server Error**: 服务器内部错误

##### 4.2.4 获取网站的广告位列表

- **接口路径**: `GET /api/v1/websites/{websiteId}/spaces`

- **描述**: 获取指定网站下的所有广告位，支持分页和筛选。

- 请求头

  :

  - `Authorization: Bearer <token>`

- 路径参数

  :

  - `websiteId` (long): 网站ID，必填

- 查询参数

  :

  - `status` (int): 广告位状态，选填（0-待审核、1-已审核、2-已拒绝）
  - `page` (int): 页码，默认值为1
  - `size` (int): 每页数量，默认值为10

- 响应

  :

  - **成功 (200 OK)**:

    ```json
    {
        "currentPage": 1,
        "pageSize": 10,
        "totalPages": 3,
        "totalItems": 25,
        "items": [
            {
                "id": 54321,
                "websiteId": 12345,
                "name": "string",
                "width": 300,
                "height": 250,
                "code": "<iframe src=\"http://yourdomain.com/ad/display/{uniquePath}\" width=\"300\" height=\"250\" frameborder=\"0\"></iframe>",
                "status": 0,
                "createTime": "2024-04-27T12:34:56Z",
                "updateTime": "2024-04-27T12:34:56Z"
            },
            // 更多广告位
        ]
    }
    ```

  - **失败**:

    - **401 Unauthorized**: 未授权访问
    - **403 Forbidden**: 无权限查看该网站的广告位
    - **404 Not Found**: 网站不存在
    - **500 Internal Server Error**: 服务器内部错误

##### 4.2.5 获取广告位嵌入代码

- **接口路径**: `GET /api/v1/spaces/{id}/code`

- **描述**: 获取指定广告位的嵌入代码（iframe 的 src）。

- 请求头

  :

  - `Authorization: Bearer <token>`

- 路径参数

  :

  - `id` (long): 广告位ID，必填

- 响应

  :

  - **成功 (200 OK)**:

    ```json
    {
        "adCode": "<iframe src=\"http://yourdomain.com/ad/display/{uniquePath}\" width=\"300\" height=\"250\" frameborder=\"0\"></iframe>"
    }
    ```

  - **失败**:

    - **401 Unauthorized**: 未授权访问
    - **403 Forbidden**: 无权限查看该广告位的代码
    - **404 Not Found**: 广告位不存在
    - **500 Internal Server Error**: 服务器内部错误

##### 4.2.6 审核广告位

###### 4.2.6.1 审核通过

- **接口路径**: `POST /api/v1/spaces/{id}/approve`

- **描述**: 管理员审核通过指定ID的广告位。

- 请求头

  :

  - `Authorization: Bearer <token>`
  - **角色要求**: 管理员

- 路径参数

  :

  - `id` (long): 广告位ID，必填

- 响应

  :

  - **成功 (200 OK)**:

    ```json
    {
        "message": "广告位已审核通过。",
        "adCode": "<iframe src=\"http://yourdomain.com/ad/display/{uniquePath}\" width=\"300\" height=\"250\" frameborder=\"0\"></iframe>"
    }
    ```

  - **失败**:

    - **401 Unauthorized**: 未授权访问
    - **403 Forbidden**: 无权限审核该广告位
    - **404 Not Found**: 广告位不存在
    - **500 Internal Server Error**: 服务器内部错误

###### 4.2.6.2 审核拒绝

- **接口路径**: `POST /api/v1/spaces/{id}/reject`

- **描述**: 管理员审核拒绝指定ID的广告位。

- 请求头

  :

  - `Authorization: Bearer <token>`
  - **角色要求**: 管理员

- 路径参数

  :

  - `id` (long): 广告位ID，必填

- 响应

  :

  - **成功 (200 OK)**:

    ```json
    {
        "message": "广告位已审核拒绝。"
    }
    ```

  - **失败**:

    - **401 Unauthorized**: 未授权访问
    - **403 Forbidden**: 无权限审核该广告位
    - **404 Not Found**: 广告位不存在
    - **500 Internal Server Error**: 服务器内部错误