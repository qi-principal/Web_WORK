package org.example.demo.Entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * ClassName: News
 * Package: org.example.demo.Entity
 * Description:
 *
 * @Author 谢依雯
 * @Create 2024/12/4 15:17
 */
@Data
@NoArgsConstructor
public class News {
    private int id;
    private String imageUrl;
    private String title;
    private String brief;
    private LocalDateTime createTime;

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
