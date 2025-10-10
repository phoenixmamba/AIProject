package com.cuijian.aimeeting.service;

import com.cuijian.aimeeting.entity.AiSessionHistory;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MeetingAiService工厂类
 * 用于管理MeetingAiService实例，避免重复创建
 * 根据用户ID和会议ID维护独立的AI服务实例和对话历史
 */
@Component
public class MeetingAiServiceFactory {
    
    @Autowired
    private ChatModel chatModel;
    
    @Autowired
    private AiSessionHistoryService aiSessionHistoryService;
    
    // 使用ConcurrentHashMap确保线程安全
    private final ConcurrentHashMap<String, MeetingAiService> serviceCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, MessageWindowChatMemory> memoryCache = new ConcurrentHashMap<>();
    
    // 控制是否打印ChatMemory内容的开关
    private static final boolean DEBUG_CHAT_MEMORY = true;
    
    /**
     * 根据用户ID和会议ID获取MeetingAiService实例
     * 
     * @param userId 用户ID
     * @param meetingId 会议ID，如果为null表示创建新会议
     * @return MeetingAiService实例
     */
    public MeetingAiService getMeetingAiService(String userId, Long meetingId) {
        // 构建缓存key
        String key = buildCacheKey(userId, meetingId);
        
        // 先尝试从缓存中获取
        MeetingAiService service = serviceCache.get(key);
        
        // 调试：打印ChatMemory内容
        if (DEBUG_CHAT_MEMORY) {
            if (service != null) {
                MessageWindowChatMemory chatMemory = memoryCache.get(key);
                if (chatMemory != null) {
                    printChatMemoryContents(chatMemory, "getMeetingAiService - 缓存命中");
                }
            } else {
                System.out.println("getMeetingAiService - 缓存未命中，用户ID: " + userId + ", 会议ID: " + meetingId);
            }
        }
        
        return service;
    }
    
    /**
     * 为新会议创建并注册MeetingAiService实例
     * 
     * @param userId 用户ID
     * @param meetingId 会议ID
     * @param service 已创建的MeetingAiService实例
     * @param chatMemory 对应的ChatMemory实例
     */
    public void registerNewMeetingService(String userId, Long meetingId, MeetingAiService service, MessageWindowChatMemory chatMemory) {
        String key = buildCacheKey(userId, meetingId);
        serviceCache.put(key, service);
        memoryCache.put(key, chatMemory);
        
        // 调试：打印ChatMemory内容
        if (DEBUG_CHAT_MEMORY) {
            printChatMemoryContents(chatMemory, "registerNewMeetingService - 用户ID: " + userId + ", 会议ID: " + meetingId);
        }
    }
    
    /**
     * 清除指定用户和会议的缓存
     * 
     * @param userId 用户ID
     * @param meetingId 会议ID
     */
    public void clearCache(String userId, Long meetingId) {
        String key = buildCacheKey(userId, meetingId);
        serviceCache.remove(key);
        memoryCache.remove(key);
    }
    
    /**
     * 清除指定用户的所有缓存
     * 
     * @param userId 用户ID
     */
    public void clearUserCache(String userId) {
        serviceCache.entrySet().removeIf(entry -> entry.getKey().startsWith(userId + ":"));
        memoryCache.entrySet().removeIf(entry -> entry.getKey().startsWith(userId + ":"));
    }
    
    /**
     * 构建缓存key
     * 
     * @param userId 用户ID
     * @param meetingId 会议ID
     * @return 缓存key
     */
    private String buildCacheKey(String userId, Long meetingId) {
        return userId + ":" + (meetingId != null ? meetingId.toString() : "new");
    }
    
    /**
     * 创建新的MeetingAiService实例（不加入缓存）
     * 
     * @param userId 用户ID
     * @param meetingId 会议ID，如果为null表示创建新会议
     * @return 新的MeetingAiService实例和对应的ChatMemory
     */
    public NewServiceInstance createNewServiceInstance(String userId, Long meetingId) {
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(20);
        
        // 如果会议ID不为null，则从数据库加载历史会话信息
        if (meetingId != null) {
            loadChatHistoryIntoMemory(userId, meetingId, chatMemory);
        }
        
        MeetingAiService service = AiServices.builder(MeetingAiService.class)
                .chatModel(chatModel)
                .chatMemory(chatMemory)
                .build();
        
        // 调试：打印ChatMemory内容
        if (DEBUG_CHAT_MEMORY) {
            printChatMemoryContents(chatMemory, "createNewServiceInstance - 用户ID: " + userId + ", 会议ID: " + meetingId);
        }
        
        return new NewServiceInstance(service, chatMemory);
    }
    
    /**
     * 从数据库加载历史会话信息到ChatMemory中
     * 
     * @param userId 用户ID
     * @param meetingId 会议ID
     * @param chatMemory ChatMemory实例
     */
    private void loadChatHistoryIntoMemory(String userId, Long meetingId, MessageWindowChatMemory chatMemory) {
        try {
            // 从数据库查询历史会话记录
            List<AiSessionHistory> historyList = aiSessionHistoryService.getSessionHistoryByMeetingId(meetingId);
            
            if (historyList != null && !historyList.isEmpty()) {
                System.out.println("加载历史会话记录到ChatMemory中，记录数: " + historyList.size());
                
                // 将历史记录按时间顺序添加到ChatMemory中
                for (AiSessionHistory history : historyList) {
                    // 添加用户消息
                    chatMemory.add(new UserMessage(history.getUserMessage()));
                    
                    // 添加AI响应消息
                    chatMemory.add(new AiMessage(history.getAiResponse()));
                }
            } else {
                System.out.println("没有找到历史会话记录，会议ID: " + meetingId);
            }
        } catch (Exception e) {
            System.err.println("加载历史会话记录时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 新服务实例包装类
     */
    public static class NewServiceInstance {
        private final MeetingAiService service;
        private final MessageWindowChatMemory chatMemory;
        
        public NewServiceInstance(MeetingAiService service, MessageWindowChatMemory chatMemory) {
            this.service = service;
            this.chatMemory = chatMemory;
        }
        
        public MeetingAiService getService() {
            return service;
        }
        
        public MessageWindowChatMemory getChatMemory() {
            return chatMemory;
        }
    }
    
    /**
     * 打印ChatMemory中的内容
     */
    private void printChatMemoryContents(MessageWindowChatMemory chatMemory, String methodName) {
        try {
            System.out.println("=== ChatMemory调试信息开始 ===");
            System.out.println("方法: " + methodName);
            
            // 获取ChatMemory中的消息
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