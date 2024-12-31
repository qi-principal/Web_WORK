package org.example.demo.Mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.demo.Entity.News;

import java.util.List;

/**
 * ClassName: NewsMapper
 * Package: org.example.demo.Mapper
 * Description:
 *
 * @Author 谢依雯
 * @Create 2024/12/4 15:55
 */
@Mapper
public interface NewsMapper {
    /**
     * 获取全部新闻信息
     * @return
     */
    @Select("select id, image_url as imageUrl, title, brief, create_time as createTime from news")
    List<News> getAll();

    @Insert("insert into news (image_url, title, brief, create_time) VALUES " +
            "(#{imageUrl},#{title},#{brief},#{createTime})")
    void put(News news);
}
