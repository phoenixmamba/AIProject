package com.cuijian.aimeeting.ai;

import com.cuijian.aimeeting.entity.AiSessionHistory;
import com.cuijian.aimeeting.entity.AppChatHistory;
import com.cuijian.aimeeting.service.AppCodeGenerateService;
import com.cuijian.aimeeting.service.MeetingAiServiceFactory;
import com.cuijian.aimeeting.utils.SpringContextUtil;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
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
public class AppAiServiceFactory {
    
    @Autowired
    private OpenAiChatModel openAiChatModel;
    
    @Autowired
    private AppChatHistoryService appChatHistoryService;
    
    // 使用ConcurrentHashMap确保线程安全
    private final ConcurrentHashMap<String, AppCodeGenerateService> serviceCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, MessageWindowChatMemory> memoryCache = new ConcurrentHashMap<>();
    
    // 控制是否打印ChatMemory内容的开关
    private static final boolean DEBUG_CHAT_MEMORY = true;
    
    /**
     * 根据用户ID和appId获取AppCodeGenerateService实例
     * 
     * @param userId 用户ID
     * @param appId 应用ID
     * @return AppCodeGenerateService实例
     */
    public AppCodeGenerateService getAppCodeGenerateService(String userId, String appId) {
        // 构建缓存key
        String key = buildCacheKey(userId, appId);
        
        // 先尝试从缓存中获取
        AppCodeGenerateService service = serviceCache.get(key);

        if (service != null) {
            MessageWindowChatMemory chatMemory = memoryCache.get(key);
            if (chatMemory != null) {
                printChatMemoryContents(chatMemory, "getAppCodeGenerateService - 缓存命中");
            }
        } else {
            System.out.println("getAppCodeGenerateService - 缓存未命中，用户ID: " + userId + ", appId: " + appId);
            NewServiceInstance newServiceInstance = createNewServiceInstance(userId, appId);
            registerNewAppCodeGenerateService(userId, appId, newServiceInstance.service, newServiceInstance.chatMemory);
            service = newServiceInstance.service;
        }
        
        return service;
    }
    
    /**
     * 为新应用创建并注册AppCodeGenerateService实例
     * 
     * @param userId 用户ID
     * @param appId appId
     * @param service 已创建的AppCodeGenerateService实例
     * @param chatMemory 对应的ChatMemory实例
     */
    public void registerNewAppCodeGenerateService(String userId, String appId, AppCodeGenerateService service, MessageWindowChatMemory chatMemory) {
        String key = buildCacheKey(userId, appId);
        serviceCache.put(key, service);
        memoryCache.put(key, chatMemory);

        printChatMemoryContents(chatMemory, "registerNewAppCodeGenerateService - 用户ID: " + userId + ", appId: " + appId);
    }

    /**
     * 清除指定用户和会议的缓存
     *
     * @param userId 用户ID
     * @param appId appId
     */
    public void clearCache(String userId, String appId) {
        String key = buildCacheKey(userId, appId);
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
     * @param appId 应用ID
     * @return 新的AppCodeGenerateService实例和对应的ChatMemory
     */
    public NewServiceInstance createNewServiceInstance(String userId,  String appId) {
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(20);
        
        // 如果应用ID不为null，则从数据库加载历史会话信息
        if (appId != null) {
            loadChatHistoryIntoMemory(userId, appId, chatMemory);
        }
        // 使用多例模式的 StreamingChatModel 解决并发问题
        StreamingChatModel openAiStreamingChatModel = SpringContextUtil.getBean("streamingChatModelPrototype", StreamingChatModel.class);
        AppCodeGenerateService service = AiServices.builder(AppCodeGenerateService.class)
                .chatModel(openAiChatModel)
                .streamingChatModel(openAiStreamingChatModel)
                .chatMemory(chatMemory)
                .build();

        printChatMemoryContents(chatMemory, "createNewServiceInstance - 用户ID: " + userId + ", appId: " + appId);


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
        try {
            // 从数据库查询历史会话记录
            List<AppChatHistory> historyList = appChatHistoryService.getChatHistoryByAppId(appId);
            
            if (historyList != null && !historyList.isEmpty()) {
                System.out.println("加载历史会话记录到ChatMemory中，记录数: " + historyList.size());
                
                // 将历史记录按时间顺序添加到ChatMemory中
                for (AppChatHistory history : historyList) {
                    // 添加用户消息
                    if(history.getMessageType().equals("user")){
                        chatMemory.add(new UserMessage(history.getMessage()));
                    }else{
                        // 添加AI响应消息
                        chatMemory.add(new AiMessage(history.getMessage()));
                    }
                }
            } else {
                System.out.println("没有找到历史会话记录，appID: " + appId);
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
        private final AppCodeGenerateService service;
        private final MessageWindowChatMemory chatMemory;
        
        public NewServiceInstance(AppCodeGenerateService service, MessageWindowChatMemory chatMemory) {
            this.service = service;
            this.chatMemory = chatMemory;
        }
        
        public AppCodeGenerateService getService() {
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