package com.cuijian.aimeeting.service;

import com.cuijian.aimeeting.entity.AiSessionHistory;
import com.cuijian.aimeeting.entity.Meeting;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/9/26 10:46
 **/
@Service
@RequiredArgsConstructor
public class AiParsingService {
    
    private final MeetingAiServiceFactory meetingAiServiceFactory;
    
    // 用于存储创建会议时的临时AI服务实例
    private final ThreadLocal<MeetingAiServiceFactory.NewServiceInstance> tempServiceInstance = new ThreadLocal<>();
    
    // 控制是否打印ChatMemory内容的开关
    private static final boolean DEBUG_CHAT_MEMORY = true;

    /**
     * 解析用户描述生成会议信息（用于创建新会议）
     */
    public Meeting parseMeetingDescription(String userId, String description) {
        // 为新会议创建新的AI服务实例，传入userId和null作为meetingId（因为是新会议）
        MeetingAiServiceFactory.NewServiceInstance newInstance = meetingAiServiceFactory.createNewServiceInstance(userId, null);
        MeetingAiService meetingAiService = newInstance.getService();
        
        // 将新创建的实例保存到ThreadLocal中，供后续注册使用
        tempServiceInstance.set(newInstance);
        
        Meeting meeting = meetingAiService.parseMeeting(description);
        
        // 调试：打印ChatMemory内容
        if (DEBUG_CHAT_MEMORY) {
            printChatMemoryContents(newInstance.getChatMemory(), "parseMeetingDescription");
        }
        
        return meeting;
    }
    
    /**
     * 在获取到会议ID后，将AI服务实例注册到工厂中
     */
    public void registerMeetingServiceWithId(String userId, Long meetingId) {
        MeetingAiServiceFactory.NewServiceInstance newInstance = tempServiceInstance.get();
        if (newInstance != null) {
            // 将实例注册到工厂中
            meetingAiServiceFactory.registerNewMeetingService(userId, meetingId, newInstance.getService(), newInstance.getChatMemory());
            
            // 清除ThreadLocal中的临时实例
            tempServiceInstance.remove();
        }
    }
    
    /**
     * 更新会议信息
     */
    public Meeting updateMeetingDescription(String userId, Long meetingId, Meeting existingMeeting, String updateDescription) {
        MeetingAiService meetingAiService = meetingAiServiceFactory.getMeetingAiService(userId, meetingId);
        if (meetingAiService == null) {
            // 如果没有找到缓存的服务实例，则创建新的（这次会加载历史记录）
            MeetingAiServiceFactory.NewServiceInstance newInstance = meetingAiServiceFactory.createNewServiceInstance(userId, meetingId);
            meetingAiService = newInstance.getService();
            // 注册到工厂中
            meetingAiServiceFactory.registerNewMeetingService(userId, meetingId, meetingAiService, newInstance.getChatMemory());
            
            // 调试：打印ChatMemory内容
            if (DEBUG_CHAT_MEMORY) {
                printChatMemoryContents(newInstance.getChatMemory(), "updateMeetingDescription - 新实例");
            }
        } else {
            // 调试：打印ChatMemory内容
            if (DEBUG_CHAT_MEMORY) {
                // 我们无法直接访问已存在的ChatMemory，但可以在接口方法中添加调试
                System.out.println("updateMeetingDescription - 使用现有实例，会议ID: " + meetingId);
            }
        }
        
        // 直接获取更新后的会议信息
        return meetingAiService.updateMeeting(updateDescription);
    }
    
    /**
     * 为已创建的会议注册AI服务实例
     * 
     * @param userId 用户ID
     * @param meetingId 会议ID
     * @param service AI服务实例
     * @param chatMemory 对应的ChatMemory
     */
    public void registerMeetingService(String userId, Long meetingId, MeetingAiService service, MessageWindowChatMemory chatMemory) {
        meetingAiServiceFactory.registerNewMeetingService(userId, meetingId, service, chatMemory);
    }

    /**
     * 基于会话历史解析用户描述生成会议信息
     */
    public Meeting parseMeetingDescriptionWithHistory(String userId, List<AiSessionHistory> history, String description) {
        // 为新会议创建新的AI服务实例，传入userId和null作为meetingId（因为是新会议）
        MeetingAiServiceFactory.NewServiceInstance newInstance = meetingAiServiceFactory.createNewServiceInstance(userId, null);
        MeetingAiService meetingAiService = newInstance.getService();
        
        // 将新创建的实例保存到ThreadLocal中，供后续注册使用
        tempServiceInstance.set(newInstance);
        
        // 将历史记录转换为文本格式
        String historyText = formatHistory(history);
        
        Meeting meeting = meetingAiService.parseMeetingWithHistory(historyText, description);
        
        // 调试：打印ChatMemory内容
        if (DEBUG_CHAT_MEMORY) {
            printChatMemoryContents(newInstance.getChatMemory(), "parseMeetingDescriptionWithHistory");
        }
        
        return meeting;
    }

    /**
     * 生成会议更新指令
     */
    public String generateUpdateInstructions(String userId, Long meetingId, String originalMeeting, String updateDescription) {
        MeetingAiService meetingAiService = meetingAiServiceFactory.getMeetingAiService(userId, meetingId);
        if (meetingAiService == null) {
            // 如果没有找到缓存的服务实例，则创建新的（这次会加载历史记录）
            MeetingAiServiceFactory.NewServiceInstance newInstance = meetingAiServiceFactory.createNewServiceInstance(userId, meetingId);
            meetingAiService = newInstance.getService();
            // 注册到工厂中
            meetingAiServiceFactory.registerNewMeetingService(userId, meetingId, meetingAiService, newInstance.getChatMemory());
            
            // 调试：打印ChatMemory内容
            if (DEBUG_CHAT_MEMORY) {
                printChatMemoryContents(newInstance.getChatMemory(), "generateUpdateInstructions - 新实例");
            }
        } else {
            // 调试：打印ChatMemory内容
            if (DEBUG_CHAT_MEMORY) {
                System.out.println("generateUpdateInstructions - 使用现有实例，会议ID: " + meetingId);
            }
        }
        return meetingAiService.generateUpdateInstruction(updateDescription);
    }

    /**
     * 基于会话历史生成会议更新指令
     */
    public String generateUpdateInstructionsWithHistory(String userId, Long meetingId, String originalMeeting, 
                                                       List<AiSessionHistory> history, String updateDescription) {
        MeetingAiService meetingAiService = meetingAiServiceFactory.getMeetingAiService(userId, meetingId);
        if (meetingAiService == null) {
            // 如果没有找到缓存的服务实例，则创建新的（这次会加载历史记录）
            MeetingAiServiceFactory.NewServiceInstance newInstance = meetingAiServiceFactory.createNewServiceInstance(userId, meetingId);
            meetingAiService = newInstance.getService();
            // 注册到工厂中
            meetingAiServiceFactory.registerNewMeetingService(userId, meetingId, meetingAiService, newInstance.getChatMemory());
            
            // 调试：打印ChatMemory内容
            if (DEBUG_CHAT_MEMORY) {
                printChatMemoryContents(newInstance.getChatMemory(), "generateUpdateInstructionsWithHistory - 新实例");
            }
        } else {
            // 调试：打印ChatMemory内容
            if (DEBUG_CHAT_MEMORY) {
                System.out.println("generateUpdateInstructionsWithHistory - 使用现有实例，会议ID: " + meetingId);
            }
        }
        
        return meetingAiService.generateUpdateInstructionWithHistory(updateDescription);
    }

    /**
     * 确认删除操作
     */
    public String confirmDeletion(String userId, Long meetingId, String meetingTitle) {
        MeetingAiService meetingAiService = meetingAiServiceFactory.getMeetingAiService(userId, meetingId);
        if (meetingAiService == null) {
            // 如果没有找到缓存的服务实例，则创建新的（这次会加载历史记录）
            MeetingAiServiceFactory.NewServiceInstance newInstance = meetingAiServiceFactory.createNewServiceInstance(userId, meetingId);
            meetingAiService = newInstance.getService();
            // 注册到工厂中
            meetingAiServiceFactory.registerNewMeetingService(userId, meetingId, meetingAiService, newInstance.getChatMemory());
            
            // 调试：打印ChatMemory内容
            if (DEBUG_CHAT_MEMORY) {
                printChatMemoryContents(newInstance.getChatMemory(), "confirmDeletion - 新实例");
            }
        } else {
            // 调试：打印ChatMemory内容
            if (DEBUG_CHAT_MEMORY) {
                System.out.println("confirmDeletion - 使用现有实例，会议ID: " + meetingId);
            }
        }
        return meetingAiService.confirmDeletion("是否要删除会议：" + meetingTitle);
    }
    
    /**
     * 将历史记录格式化为文本
     */
    private String formatHistory(List<AiSessionHistory> history) {
        if (history == null || history.isEmpty()) {
            return "无历史记录";
        }
        
        return history.stream()
                .map(h -> "用户: " + h.getUserMessage() + "\nAI: " + h.getAiResponse())
                .collect(Collectors.joining("\n---\n"));
    }
    
    /**
     * 清除指定用户和会议的缓存
     */
    public void clearCache(String userId, Long meetingId) {
        meetingAiServiceFactory.clearCache(userId, meetingId);
    }
    
    /**
     * 清除指定用户的所有缓存
     */
    public void clearUserCache(String userId) {
        meetingAiServiceFactory.clearUserCache(userId);
    }
    
    /**
     * 打印ChatMemory中的内容
     */
    private void printChatMemoryContents(MessageWindowChatMemory chatMemory, String methodName) {
        try {
            System.out.println("=== ChatMemory调试信息开始 ===");
            System.out.println("方法: " + methodName);
            
            // 通过反射获取ChatMemory中的消息
            List<ChatMessage> messages = chatMemory.messages();
            System.out.println("消息数量: " + messages.size());
            
            for (int i = 0; i < messages.size(); i++) {
                ChatMessage message = messages.get(i);
                System.out.println("消息 " + i + " - 类型: " + message.type() + ", 内容: " + message);
            }
            
            System.out.println("=== ChatMemory调试信息结束 ===");
        } catch (Exception e) {
            System.out.println("无法打印ChatMemory内容: " + e.getMessage());
        }
    }
}