package com.cuijian.aimeeting.service;

import com.cuijian.aimeeting.entity.Meeting;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/9/26 10:46
 **/
@Service
public class AiParsingService {
    @Resource(name = "openAiChatModel")
    private ChatModel chatModel;

    /**
     * 解析用户描述生成会议信息
     */
    public Meeting parseMeetingDescription(String description) {
        MeetingAiService meetingAiService = AiServices.builder(MeetingAiService.class).chatModel(chatModel)
                .build();
        return meetingAiService.parseMeeting(description);
    }

    /**
     * 生成会议更新指令
     */
    public String generateUpdateInstructions(String originalMeeting, String updateDescription) {
        MeetingAiService meetingAiService = AiServices.builder(MeetingAiService.class).chatModel(chatModel)
                .build();
        return meetingAiService.generateUpdateInstruction(originalMeeting, updateDescription);
    }

    /**
     * 确认删除操作
     */
    public String confirmDeletion(String meetingTitle) {
        MeetingAiService meetingAiService = AiServices.builder(MeetingAiService.class).chatModel(chatModel)
                .build();
        return meetingAiService.confirmDeletion("是否要删除会议：" + meetingTitle);
    }
}
