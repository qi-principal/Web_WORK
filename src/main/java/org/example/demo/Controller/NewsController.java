package org.example.demo.Controller;

import org.example.demo.Entity.News;
import org.example.demo.Entity.Result;
import org.example.demo.Service.NewsService;
import org.example.demo.util.AliOssUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * ClassName: NewsController
 * Package: org.example.demo.Controller
 * Description:
 *
 * @Author 谢依雯
 * @Create 2024/12/4 15:24
 */
@RestController
@RequestMapping("/api/news")
@CrossOrigin
public class NewsController {
    private static final Logger log = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    private NewsService newsService;
    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 获取全部新闻
     * @return 新闻列表
     */
    @GetMapping("/all")
    public Result<List<News>> getAll(){
        return newsService.getAll();
    }

    /**
     * 获取政��新闻
     * @return 政治新闻列表
     */
    @GetMapping("/politics")
    public Result<List<News>> getPoliticsNews() {
        return newsService.getNewsByCategory("政治");
    }

    /**
     * 获取经济新闻
     * @return 经济新闻列表
     */
    @GetMapping("/economy")
    public Result<List<News>> getEconomyNews() {
        return newsService.getNewsByCategory("经济");
    }

    /**
     * 获取法制新闻
     * @return 法制新闻列表
     */
    @GetMapping("/law")
    public Result<List<News>> getLawNews() {
        return newsService.getNewsByCategory("法制");
    }

    /**
     * 获取文娱新闻
     * @return 文娱新闻列表
     */
    @GetMapping("/entertainment")
    public Result<List<News>> getEntertainmentNews() {
        return newsService.getNewsByCategory("文娱");
    }

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + substring;

        try {
            String filePath = aliOssUtil.upload(file.getBytes(), filename);
            return Result.success(filePath);
        } catch (IOException e) {
            log.info("文件上传失败");
        }
        return Result.error("上传失败");
    }

    /**
     * 上传新闻信息
     * @param news 新闻对象
     * @return 操作结果
     */
    @PostMapping("/put")
    public Result put(News news){
        if(news.getImageUrl() == null || news.getImageUrl().trim().isEmpty()) {
            news.setImageUrl("default_news_image.jpg");
        }
        newsService.put(news);
        return Result.success();
    }

    /**
     * 搜索新闻
     * @param keyword 搜索关键字
     * @return 搜索结果
     */
    @GetMapping("/search")
    public Result<List<News>> searchNews(@RequestParam String keyword) {
        log.info("搜索新闻，关键字: {}", keyword);
        return newsService.searchNewsByKeyword(keyword);
    }
}
