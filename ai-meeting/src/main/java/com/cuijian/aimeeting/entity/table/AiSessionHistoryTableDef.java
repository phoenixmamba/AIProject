package com.cuijian.aimeeting.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

/**
 * AI会话历史表定义
 * 
 * @author cui_jian
 * @since 2025-10-10
 */
public class AiSessionHistoryTableDef extends TableDef {

    /**
     * AI会话历史表定义
     */
    public static final AiSessionHistoryTableDef AI_SESSION_HISTORY = new AiSessionHistoryTableDef();

    /**
     * 主键ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 会议ID，关联meeting表
     */
    public final QueryColumn MEETING_ID = new QueryColumn(this, "meeting_id");

    /**
     * 用户ID
     */
    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

    /**
     * 用户输入内容
     */
    public final QueryColumn USER_MESSAGE = new QueryColumn(this, "user_message");

    /**
     * AI响应内容
     */
    public final QueryColumn AI_RESPONSE = new QueryColumn(this, "ai_response");

    /**
     * 会话类型：CREATE(创建会议)、UPDATE(更新会议)、DELETE(删除会议)
     */
    public final QueryColumn SESSION_TYPE = new QueryColumn(this, "session_type");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 构造方法
     */
    protected AiSessionHistoryTableDef(String schema, String tableName) {
        super(schema, tableName);
    }

    /**
     * 构造方法
     */
    protected AiSessionHistoryTableDef(String schema, String tableName, String alias) {
        super(schema, tableName, alias);
    }

    /**
     * 构造方法
     */
    public AiSessionHistoryTableDef() {
        super("meeting_ai", "ai_session_history");
    }

}