package com.cuijian.aimeeting.service;

import com.cuijian.aimeeting.entity.Meeting;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/9/26 10:45
 **/
public interface MeetingAiService {
    @SystemMessage("你是一个会议信息解析专家，能够从自然语言描述中提取会议关键信息。" +
            "请严格按照Meeting类的结构返回数据，" +
            "相对时间（如明天、后天）需基于【当前时间】解析，"+
            "创建时间和更新时间为空时可以默认为当前时间，确保时间格式为yyyy-MM-dd HH:mm:ss，" +
            "多个部门和领导用逗号分隔。如果信息不完整，使用合理默认值或空字符串。")
    Meeting parseMeeting(String description);

    @SystemMessage("你是一个会议更新助手，需要根据原始会议信息和更新描述，" +
            "生成清晰的会议更新指令，保持与原始信息格式一致，"+
            "相对时间（如明天、后天）需基于【当前时间】解析，创建时间无需修改，更新时间默认为当前时间")
    @UserMessage("原始会议信息：{{original}}\n，更新描述：{{updateDescription}}。")
    String generateUpdateInstruction(@V("original") String original, @V("updateDescription") String updateDescription);

    @SystemMessage("你是一个确认助手，需要判断用户是否真的要删除指定会议，" +
            "如果描述中包含删除、移除等明确指令，返回'确认删除'，否则返回'取消删除'。")
    String confirmDeletion(String meetingTitle);
}
