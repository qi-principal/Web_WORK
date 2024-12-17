package com.web.ads.mapper;

import com.web.ads.entity.AdTaskRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 广告任务记录数据访问层
 */
@Mapper
public interface AdTaskRecordMapper {
    /**
     * 插入记录
     */
    @Insert("INSERT INTO ad_task_record (task_id, record_type, record_time, client_ip, user_agent) " +
            "VALUES (#{taskId}, #{recordType}, #{recordTime}, #{clientIp}, #{userAgent})")
    @Options(useGeneratedKeys = true, keyProperty = "recordId")
    int insert(AdTaskRecord record);

    /**
     * 根据任务ID查询记录
     */
    @Select("SELECT * FROM ad_task_record WHERE task_id = #{taskId}")
    List<AdTaskRecord> selectByTaskId(@Param("taskId") Integer taskId);

    /**
     * 根据任务ID和记录类型统计记录数
     */
    @Select("SELECT COUNT(*) FROM ad_task_record " +
            "WHERE task_id = #{taskId} AND record_type = #{recordType}")
    int countByTaskIdAndType(@Param("taskId") Integer taskId, 
                            @Param("recordType") String recordType);

    /**
     * 查询指定时间范围内的记录
     */
    @Select("SELECT * FROM ad_task_record " +
            "WHERE task_id = #{taskId} " +
            "AND record_time BETWEEN #{startTime} AND #{endTime}")
    List<AdTaskRecord> selectByTimeRange(@Param("taskId") Integer taskId,
                                       @Param("startTime") String startTime,
                                       @Param("endTime") String endTime);
}