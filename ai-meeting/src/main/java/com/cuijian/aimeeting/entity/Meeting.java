package com.cuijian.aimeeting.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/9/26 10:22
 **/
@Data
@Table("meeting")  // 指定数据库表名
public class Meeting {
    @Id(keyType = KeyType.Auto)  // 自动增长主键
    private Long id;
    private String title;
    private String content;
    private String meetingTime;
    private String location;
    private String departments;
    private String leaders;

    private String createTime;
    private String updateTime;
}
