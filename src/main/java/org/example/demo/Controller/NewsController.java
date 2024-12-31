package org.example.demo.Controller;

import org.example.demo.Entity.News;
import org.example.demo.Entity.Result;
import org.example.demo.Service.NewsService;
import org.example.demo.util.AliOssUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * ClassName: NewsController
 * Package: org.example.demo.Controller
 * Description:
 *
 * @Author 
 * @Create 2024/12/4 15:24
 */
@RestController
@RequestMapping("/news")
@CrossOrigin
public class NewsController {
    private static final Logger log = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    private NewsService newsService;
    @Autowired
    private AliOssUtil aliOssUtil;
    /**
     * 获取全部新闻
     * @return
     */
    @GetMapping("/all")
    public Result<List<News>> getAll(){
            return newsService.getAll();
    }

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        String originalFilename=file.getOriginalFilename();
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename= UUID.randomUUID().toString()+substring;

        try {
            String filePath= aliOssUtil.upload(file.getBytes(),filename);
            return Result.success(filePath);
        } catch (IOException e) {
            log.info("文件上传失败");
        }
        return Result.error("上传失败");
    }

    /**
     * 上传新闻信息
     * @param news
     * @return
     */
    @PostMapping("/put")
    public Result put(News news){
        if(news.getImageUrl() == null || news.getImageUrl().trim().isEmpty()) {
            news.setImageUrl("default_news_image.jpg");
        }
        newsService.put(news);
        return Result.success();
    }
}
