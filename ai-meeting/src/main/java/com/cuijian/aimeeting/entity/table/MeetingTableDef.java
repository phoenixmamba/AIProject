package com.cuijian.aimeeting.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/9/26 11:16
 **/
public class MeetingTableDef extends TableDef {

    public static final MeetingTableDef MEETING = new MeetingTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");
    public final QueryColumn TITLE = new QueryColumn(this, "title");
    public final QueryColumn CONTENT = new QueryColumn(this, "content");
    public final QueryColumn MEETING_TIME = new QueryColumn(this, "meeting_time");
    public final QueryColumn LOCATION = new QueryColumn(this, "location");
    public final QueryColumn DEPARTMENTS = new QueryColumn(this, "departments");
    public final QueryColumn LEADERS = new QueryColumn(this, "leaders");
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    protected MeetingTableDef(String schema, String tableName) {
        super(schema, tableName);
    }

    protected MeetingTableDef(String schema, String tableName, String alias) {
        super(schema, tableName, alias);
    }

    public MeetingTableDef() {
        super("meeting_ai","meeting");
    }
}
