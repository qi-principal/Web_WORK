package org.example.demo.Service.impl;

import org.example.demo.Entity.News;
import org.example.demo.Entity.Result;
import org.example.demo.Mapper.NewsMapper;
import org.example.demo.Service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ClassName: NewsServiceImpl
 * Package: org.example.demo.Service.impl
 * Description: 新闻服务实现类
 *
 * @Author 谢依雯
 * @Create 2024/12/4 15:55
 */
@Service
public class NewsServiceImpl implements NewsService {
    private static final Logger log = LoggerFactory.getLogger(NewsServiceImpl.class);

    @Autowired
    private NewsMapper newsMapper;

    @Override
    public Result<List<News>> getAll() {
        log.info("获取所有新闻");
        try {
            List<News> newsList = newsMapper.getAll();
            log.info("成功获取所有新闻，数量: {}", newsList.size());
            return Result.success(newsList);
        } catch (Exception e) {
            log.error("获取所有新闻失败", e);
            return Result.error("获取新闻失败");
        }
    }

    @Override
    public Result<List<News>> getNewsByCategory(String category) {
        log.info("获取分类新闻, 分类: {}", category);
        try {
            List<News> newsList = newsMapper.getNewsByCategory(category);
            log.info("成功获取{}分类新闻，数量: {}", category, newsList.size());
            return Result.success(newsList);
        } catch (Exception e) {
            log.error("获取分类新闻失败, 分类: " + category, e);
            return Result.error("获取新闻失败");
        }
    }

    @Override
    public void put(News news) {
        log.info("添加新闻: {}", news.getTitle());
        try {
            if (news.getCreateTime() == null) {
                news.setCreateTime(LocalDateTime.now());
            }
            newsMapper.put(news);
            log.info("新闻添加成功: {}", news.getTitle());
        } catch (Exception e) {
            log.error("新闻添加失败: " + news.getTitle(), e);
            throw e;
        }
    }

    @Override
    public Result<List<News>> searchNewsByKeyword(String keyword) {
        log.info("开始搜索新闻，关键字: {}", keyword);
        try {
            List<News> newsList = newsMapper.searchNewsByKeyword(keyword);
            log.info("搜索完成，找到 {} 条结果", newsList.size());
            return Result.success(newsList);
        } catch (Exception e) {
            log.error("搜索新闻时发生错误，关键字: " + keyword, e);
            return Result.error("搜索新闻失败: " + e.getMessage());
        }
    }
}
