package com.cuijian.aimeeting.controller;

import com.cuijian.aimeeting.entity.AiSessionHistory;
import com.cuijian.aimeeting.service.AiSessionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI会话历史控制器
 * 提供会话历史的查询接口
 */
@RestController
@RequestMapping("/api/session-history")
@RequiredArgsConstructor
public class AiSessionHistoryController {
    
    private final AiSessionHistoryService aiSessionHistoryService;
    
    /**
     * 根据会议ID获取会话历史记录
     * 
     * @param meetingId 会议ID
     * @return 会话历史记录列表
     */
    @GetMapping("/meeting/{meetingId}")
    public ResponseEntity<List<AiSessionHistory>> getSessionHistoryByMeetingId(@PathVariable Long meetingId) {
        List<AiSessionHistory> historyList = aiSessionHistoryService.getSessionHistoryByMeetingId(meetingId);
        return ResponseEntity.ok(historyList);
    }
    
    /**
     * 根据用户ID获取会话历史记录
     * 
     * @param userId 用户ID
     * @return 会话历史记录列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AiSessionHistory>> getSessionHistoryByUserId(@PathVariable String userId) {
        List<AiSessionHistory> historyList = aiSessionHistoryService.getSessionHistoryByUserId(userId);
        return ResponseEntity.ok(historyList);
    }
}