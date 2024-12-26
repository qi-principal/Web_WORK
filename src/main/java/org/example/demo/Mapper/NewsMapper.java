package org.example.demo.Mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.demo.Entity.News;

import java.util.List;

/**
 * ClassName: NewsMapper
 * Package: org.example.demo.Mapper
 * Description: 新闻数据访问接口
 *
 * @Author 谢依雯
 * @Create 2024/12/4 15:55
 */
@Mapper
public interface NewsMapper {
    /**
     * 获取全部新闻信息
     * @return 新闻列表
     */
    @Select("select id, category, image as imageUrl, title, `describe` as brief, create_time as createTime from news1")
    List<News> getAll();

    /**
     * 按分类获取新闻信息
     * @param category 新闻分类
     * @return 新闻列表
     */
    @Select("select id, category, image as imageUrl, title, `describe` as brief, create_time as createTime " +
            "from news1 where category = #{category} order by create_time desc")
    List<News> getNewsByCategory(String category);

    /**
     * 添加新闻
     * @param news 新闻对象
     */
    @Insert("insert into news1 (category, image, title, `describe`, create_time) VALUES " +
            "(#{category}, #{imageUrl}, #{title}, #{brief}, #{createTime})")
    void put(News news);

    /**
     * 按关键字搜索新闻
     * @param keyword 关键字
     * @return 新闻列表
     */
    @Select("select id, category, image as imageUrl, title, `describe` as brief, create_time as createTime " +
            "from news1 where title like concat('%', #{keyword}, '%') order by create_time desc")
    List<News> searchNewsByKeyword(String keyword);
}
