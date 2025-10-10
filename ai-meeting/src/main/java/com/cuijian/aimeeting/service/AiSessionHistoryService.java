package com.cuijian.aimeeting.service;

import com.cuijian.aimeeting.entity.AiSessionHistory;
import com.cuijian.aimeeting.mapper.AiSessionHistoryMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.cuijian.aimeeting.entity.table.AiSessionHistoryTableDef.AI_SESSION_HISTORY;


/**
 * AI会话历史服务类
 * 提供会话历史的增删查改功能
 */
@Service
@RequiredArgsConstructor
public class AiSessionHistoryService {
    
    private final AiSessionHistoryMapper aiSessionHistoryMapper;
    
    /**
     * 保存会话历史记录
     * 
     * @param meetingId 会议ID
     * @param userId 用户ID
     * @param userMessage 用户消息
     * @param aiResponse AI响应
     * @param sessionType 会话类型
     * @return 保存的会话历史记录
     */
    public AiSessionHistory saveSessionHistory(Long meetingId, String userId, String userMessage, 
                                             String aiResponse, String sessionType) {
        AiSessionHistory history = new AiSessionHistory();
        history.setMeetingId(meetingId);
        history.setUserId(userId);
        history.setUserMessage(userMessage);
        history.setAiResponse(aiResponse);
        history.setSessionType(sessionType);
        history.setCreateTime(LocalDateTime.now());
        
        aiSessionHistoryMapper.insertSelective(history);
        return history;
    }
    
    /**
     * 根据会议ID获取会话历史记录
     * 
     * @param meetingId 会议ID
     * @return 会话历史记录列表
     */
    public List<AiSessionHistory> getSessionHistoryByMeetingId(Long meetingId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .from(AI_SESSION_HISTORY)
                .where(AI_SESSION_HISTORY.MEETING_ID.eq(meetingId))
                .orderBy(AI_SESSION_HISTORY.CREATE_TIME, true); // 按创建时间升序
        
        return aiSessionHistoryMapper.selectListByQuery(queryWrapper);
    }
    
    /**
     * 根据用户ID获取会话历史记录
     * 
     * @param userId 用户ID
     * @return 会话历史记录列表
     */
    public List<AiSessionHistory> getSessionHistoryByUserId(String userId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .from(AI_SESSION_HISTORY)
                .where(AI_SESSION_HISTORY.USER_ID.eq(userId))
                .orderBy(AI_SESSION_HISTORY.CREATE_TIME, true); // 按创建时间升序
        
        return aiSessionHistoryMapper.selectListByQuery(queryWrapper);
    }
    
    /**
     * 删除指定会议的会话历史记录
     * 
     * @param meetingId 会议ID
     */
    public void deleteSessionHistoryByMeetingId(Long meetingId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .from(AI_SESSION_HISTORY)
                .where(AI_SESSION_HISTORY.MEETING_ID.eq(meetingId));
        
        aiSessionHistoryMapper.deleteByQuery(queryWrapper);
    }
}