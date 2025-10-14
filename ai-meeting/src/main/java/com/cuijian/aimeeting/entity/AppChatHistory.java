package com.cuijian.aimeeting.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import java.io.Serial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对话历史 实体类。
 *
 * @author user
 * @since 2025-10-14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("app_chat_history")
public class AppChatHistory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id(keyType = KeyType.Auto)
    private String id;

    /**
     * 消息
     */
    private String message;

    /**
     * user/ai
     */
    @Column("messageType")
    private String messageType;

    /**
     * 应用id
     */
    @Column("appId")
    private String appId;

    /**
     * 创建用户id
     */
    @Column("userId")
    private String userId;

    /**
     * 创建时间
     */
    @Column("createTime")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column("updateTime")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @Column("isDelete")
    private Integer isDelete;

}
