package com.cuijian.aimeeting.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI会话历史记录实体类
 * 用于保存用户与AI的交互记录
 */
@Data
@Table("ai_session_history")
public class AiSessionHistory {
    /**
     * 主键ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;
    
    /**
     * 会议ID，关联meeting表
     */
    private Long meetingId;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 用户输入内容
     */
    private String userMessage;
    
    /**
     * AI响应内容
     */
    private String aiResponse;
    
    /**
     * 会话类型：CREATE(创建会议)、UPDATE(更新会议)、DELETE(删除会议)
     */
    private String sessionType;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    @Override
    public String toString() {
        return "用户: " + userMessage + "\nAI: " + aiResponse;
    }
}