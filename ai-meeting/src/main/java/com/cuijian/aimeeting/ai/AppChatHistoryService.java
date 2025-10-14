package com.cuijian.aimeeting.ai;

import com.cuijian.aimeeting.entity.AppChatHistory;
import com.cuijian.aimeeting.mapper.ChatHistoryMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.cuijian.aimeeting.entity.table.ChatHistoryTableDef.CHAT_HISTORY;

/**
 * 应用对话历史服务类
 * 提供应用对话历史的增删查改功能
 */
@Service
@RequiredArgsConstructor
public class AppChatHistoryService {

    private final ChatHistoryMapper chatHistoryMapper;

    /**
     * 保存应用对话历史记录
     *
     * @param appId       应用ID
     * @param userId      用户ID
     * @param message     消息内容
     * @param messageType 消息类型 (user/ai)
     * @return 保存的对话历史记录
     */
    public AppChatHistory saveChatHistory(String appId, String userId, String message, String messageType) {
        AppChatHistory chatHistory = new AppChatHistory();
        chatHistory.setAppId(appId);
        chatHistory.setUserId(userId);
        chatHistory.setMessage(message);
        chatHistory.setMessageType(messageType);
        chatHistory.setCreateTime(LocalDateTime.now());
        chatHistory.setUpdateTime(LocalDateTime.now());
        chatHistory.setIsDelete(0);

        chatHistoryMapper.insertSelective(chatHistory);
        return chatHistory;
    }

    /**
     * 根据应用ID获取对话历史记录
     *
     * @param appId 应用ID
     * @return 对话历史记录列表
     */
    public List<AppChatHistory> getChatHistoryByAppId(String appId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .from(CHAT_HISTORY)
                .where(CHAT_HISTORY.APP_ID.eq(appId))
                .orderBy(CHAT_HISTORY.CREATE_TIME, true); // 按创建时间升序

        return chatHistoryMapper.selectListByQuery(queryWrapper);
    }

    /**
     * 根据应用ID和消息类型获取对话历史记录
     *
     * @param appId 应用ID
     * @param messageType 消息类型
     * @return 对话历史记录列表
     */
    public List<AppChatHistory> getChatHistoryByAppIdAndMessageType(String appId,String messageType) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .from(CHAT_HISTORY)
                .where(CHAT_HISTORY.APP_ID.eq(appId))
                .where(CHAT_HISTORY.MESSAGE_TYPE.eq(messageType))
                .orderBy(CHAT_HISTORY.CREATE_TIME, true); // 按创建时间升序

        return chatHistoryMapper.selectListByQuery(queryWrapper);
    }

    /**
     * 根据用户ID获取对话历史记录
     *
     * @param userId 用户ID
     * @return 对话历史记录列表
     */
    public List<AppChatHistory> getChatHistoryByUserId(String userId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .from(CHAT_HISTORY)
                .where(CHAT_HISTORY.USER_ID.eq(userId))
                .orderBy(CHAT_HISTORY.CREATE_TIME, true); // 按创建时间升序

        return chatHistoryMapper.selectListByQuery(queryWrapper);
    }

    /**
     * 删除指定应用的对话历史记录
     *
     * @param appId 应用ID
     */
    public void deleteChatHistoryByAppId(String appId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .from(CHAT_HISTORY)
                .where(CHAT_HISTORY.APP_ID.eq(appId));

        chatHistoryMapper.deleteByQuery(queryWrapper);
    }
}