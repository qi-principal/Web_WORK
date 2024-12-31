package org.example.demo.Service.impl;

import org.example.demo.Entity.News;
import org.example.demo.Entity.Result;
import org.example.demo.Mapper.NewsMapper;
import org.example.demo.Service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ClassName: NewsServiceImpl
 * Package: org.example.demo.Service.impl
 * Description:
 *
 * @Author 谢依雯
 * @Create 2024/12/4 15:55
 */
@Service
public class NewsServiceImpl implements NewsService {
    @Autowired
    private NewsMapper newsMapper;
    /**
     *获取所有新闻
     * @return
     */
    public Result<List<News>> getAll() {
        List<News> newsList = newsMapper.getAll();
        // 添加打印语句，查看获取的数据
        System.out.println("从数据库获取的新闻列表：");
        for (News news : newsList) {
            System.out.println("新闻ID: " + news.getId());
            System.out.println("标题: " + news.getTitle());
            System.out.println("简介: " + news.getBrief());
            System.out.println("图片URL: " + news.getImageUrl());
            System.out.println("创建时间: " + news.getCreateTime());
            System.out.println("------------------------");
        }
        return Result.success(newsList);
    }

    /**
     * 上传新闻信息
     * @param news
     */
    public void put(News news) {
        news.setCreateTime(LocalDateTime.now());
        newsMapper.put(news);
    }
}
