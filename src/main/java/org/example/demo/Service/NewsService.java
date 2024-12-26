package org.example.demo.Service;

import org.example.demo.Entity.News;
import org.example.demo.Entity.Result;

import java.util.List;

/**
 * ClassName: NewsService
 * Package: org.example.demo.Service.Impl
 * Description:
 *
 * @Author 谢依雯
 * @Create 2024/12/4 15:54
 */
public interface NewsService {
    Result<List<News>> getAll();

    Result<List<News>> getNewsByCategory(String category);

    void put(News news);

    /**
     * 根据关键字搜索新闻
     * @param keyword 搜索关键字
     * @return 新闻列表
     */
    Result<List<News>> searchNewsByKeyword(String keyword);
}
