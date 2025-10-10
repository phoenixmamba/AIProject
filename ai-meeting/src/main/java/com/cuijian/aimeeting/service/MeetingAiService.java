package com.cuijian.aimeeting.service;

import com.cuijian.aimeeting.entity.Meeting;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.util.List;

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

    @SystemMessage("你是一个会议信息解析专家，能够从自然语言描述中提取会议关键信息。" +
            "请参考之前的对话历史来更好地理解用户当前的意图。" +
            "请严格按照Meeting类的结构返回数据，" +
            "相对时间（如明天、后天）需基于【当前时间】解析，"+
            "创建时间和更新时间为空时可以默认为当前时间，确保时间格式为yyyy-MM-dd HH:mm:ss，" +
            "多个部门和领导用逗号分隔。如果信息不完整，使用合理默认值或空字符串。")
    @UserMessage({
        "对话历史:",
        "{{history}}",
        "当前用户请求: {{description}}"
    })
    Meeting parseMeetingWithHistory(@V("history") String history, @V("description") String description);
    
    @SystemMessage("你是一个会议更新助手，你的任务是根据用户的更新描述直接返回更新后的完整会议信息。" +
            "请参考之前的对话历史来获取已有会议信息并更好地理解用户当前的意图。" +
            "请严格按照Meeting类的结构返回更新后的数据，" +
            "用户未提及的会议信息保持不变，" +
            "相对时间（如明天、后天）需基于【当前时间】解析，"+
            "更新时间默认为当前时间，确保时间格式为yyyy-MM-dd HH:mm:ss，" +
            "多个部门和领导用逗号分隔。如果信息不完整，使用合理默认值或空字符串。")
//    @UserMessage("原始会议信息: {{existingMeeting}}\n更新描述: {{updateDescription}}")
//    Meeting updateMeeting(@V("existingMeeting") Meeting existingMeeting, @V("updateDescription") String updateDescription);
    @UserMessage("更新描述: {{updateDescription}}")
    Meeting updateMeeting(@V("updateDescription") String updateDescription);

    @SystemMessage("你是一个会议更新助手，需要根据用户的更新描述来更新会议信息。" +
            "请参考之前的对话历史来更好地理解用户当前的意图。" +
            "请严格按照Meeting类的结构返回更新后的数据，" +
            "相对时间（如明天、后天）需基于【当前时间】解析，"+
            "更新时间默认为当前时间，确保时间格式为yyyy-MM-dd HH:mm:ss，" +
            "多个部门和领导用逗号分隔。如果信息不完整，使用合理默认值或空字符串。")
    @UserMessage("请根据以下更新描述修改会议信息: {{updateDescription}}")
    String generateUpdateInstruction(@V("updateDescription") String updateDescription);

    @SystemMessage("你是一个会议更新助手，需要根据用户的更新描述来更新会议信息。" +
            "请参考之前的对话历史来更好地理解用户当前的意图。" +
            "请严格按照Meeting类的结构返回更新后的数据，" +
            "相对时间（如明天、后天）需基于【当前时间】解析，"+
            "更新时间默认为当前时间，确保时间格式为yyyy-MM-dd HH:mm:ss，" +
            "多个部门和领导用逗号分隔。如果信息不完整，使用合理默认值或空字符串。")
    @UserMessage("更新描述：{{updateDescription}}")
    String generateUpdateInstructionWithHistory(@V("updateDescription") String updateDescription);

    @SystemMessage("你是一个确认助手，需要判断用户是否真的要删除指定会议，" +
            "如果描述中包含删除、移除等明确指令，返回'确认删除'，否则返回'取消删除'。")
    String confirmDeletion(String meetingTitle);
}