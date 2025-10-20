package com.cuijian.aimeeting.ai;


import com.cuijian.aimeeting.service.PromptGenerateService;

import com.cuijian.aimeeting.utils.SpringContextUtil;
import dev.langchain4j.data.message.ChatMessage;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
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
public class PromptMasterServiceFactory {
    
    @Autowired
    private OpenAiChatModel openAiChatModel;

    
    // 使用ConcurrentHashMap确保线程安全
    private final ConcurrentHashMap<String, PromptGenerateService> serviceCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, MessageWindowChatMemory> memoryCache = new ConcurrentHashMap<>();
    
    // 控制是否打印ChatMemory内容的开关
    private static final boolean DEBUG_CHAT_MEMORY = true;
    
    /**
     * 根据对话id获取AppCodeGenerateService实例
     *
     * @param userId 用户id
     * @param id 对话id
     * @return PromptMasterService实例
     */
    public PromptGenerateService getPromptMasterService(String userId, String id) {
        // 构建缓存key
        String key = buildCacheKey(userId, id);
        
        // 先尝试从缓存中获取
        PromptGenerateService service = serviceCache.get(key);

        if (service != null) {
            MessageWindowChatMemory chatMemory = memoryCache.get(key);
            if (chatMemory != null) {
                printChatMemoryContents(chatMemory, "getPromptMasterService - 缓存命中");
            }
        } else {
            System.out.println("getPromptMasterService - 缓存未命中，用户ID: " + userId + ", 对话id: " + id);
            NewServiceInstance newServiceInstance = createNewServiceInstance(userId, id);
            registerNewPromptMasterService(userId, id, newServiceInstance.service, newServiceInstance.chatMemory);
            service = newServiceInstance.service;
        }
        
        return service;
    }
    
    /**
     * 为新应用创建并注册AppCodeGenerateService实例
     * 
     * @param userId 用户ID
     * @param id 会话Id
     * @param service 已创建的PromptMasterService实例
     * @param chatMemory 对应的ChatMemory实例
     */
    public void registerNewPromptMasterService(String userId, String id, PromptGenerateService service, MessageWindowChatMemory chatMemory) {
        String key = buildCacheKey(userId, id);
        serviceCache.put(key, service);
        memoryCache.put(key, chatMemory);

        printChatMemoryContents(chatMemory, "registerNewPromptMasterService - 用户ID: " + userId + ", 会话id: " + id);
    }

    /**
     * 清除指定用户和会议的缓存
     *
     * @param userId 用户ID
     * @param id 会话Id
     */
    public void clearCache(String userId, String id) {
        String key = buildCacheKey(userId, id);
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
     * @param appId 应用ID
     * @return 缓存key
     */
    private String buildCacheKey(String userId, String appId) {
        return userId + ":" + appId;
    }
    
    /**
     * 创建新的AppCodeGenerateService实例（不加入缓存）
     * 
     * @param userId 用户ID
     * @param id 会话ID
     * @return 新的AppCodeGenerateService实例和对应的ChatMemory
     */
    public NewServiceInstance createNewServiceInstance(String userId,  String id) {
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(20);
        
        // 如果应用ID不为null，则从数据库加载历史会话信息
//        if (id != null) {
//            loadChatHistoryIntoMemory(userId, id, chatMemory);
//        }
        // 使用多例模式的 StreamingChatModel 解决并发问题
        StreamingChatModel openAiStreamingChatModel = SpringContextUtil.getBean("streamingChatModelPrototype", StreamingChatModel.class);
        PromptGenerateService service = AiServices.builder(PromptGenerateService.class)
                .chatModel(openAiChatModel)
                .streamingChatModel(openAiStreamingChatModel)
                .chatMemory(chatMemory)
                .build();

        printChatMemoryContents(chatMemory, "createNewServiceInstance - 用户ID: " + userId + ", 会话id: " + id);


        return new NewServiceInstance(service, chatMemory);
    }
    
    /**
     * 从数据库加载历史会话信息到ChatMemory中
     * 
     * @param userId 用户ID
     * @param appId 应用ID
     * @param chatMemory ChatMemory实例
     */
    private void loadChatHistoryIntoMemory(String userId, String appId, MessageWindowChatMemory chatMemory) {

    }
    
    /**
     * 新服务实例包装类
     */
    public static class NewServiceInstance {
        private final PromptGenerateService service;
        private final MessageWindowChatMemory chatMemory;
        
        public NewServiceInstance(PromptGenerateService service, MessageWindowChatMemory chatMemory) {
            this.service = service;
            this.chatMemory = chatMemory;
        }
        
        public PromptGenerateService getService() {
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