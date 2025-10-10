package com.cuijian.aimeeting.service;

import com.cuijian.aimeeting.mapper.MeetingMapper;
import com.cuijian.aimeeting.entity.AiSessionHistory;
import com.cuijian.aimeeting.entity.Meeting;
import com.cuijian.aimeeting.entity.table.MeetingTableDef;
import com.cuijian.aimeeting.monitor.MonitorContextHolder;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/9/26 10:47
 **/
@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingMapper meetingMapper;
    private final AiParsingService aiParsingService;
    private final AiSessionHistoryService aiSessionHistoryService;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Meeting createMeetingByDescription(String description) {
        String userId = MonitorContextHolder.get().getUserId();
        
        // 通过AI解析用户描述生成会议对象
        String currentTime = "【当前时间】：" + LocalDateTime.now().format(TIME_FORMATTER);
        String fullContext = currentTime + "\n【会议描述】：" + description;
        
        Meeting meeting = aiParsingService.parseMeetingDescription(userId, fullContext);
        
        meetingMapper.insert(meeting);  // MyBatis-Flex的插入方法
        
        // 更新MonitorContext中的会议ID
        MonitorContextHolder.get().setMeetingId(meeting.getId());
        
        // 在获取到会议ID后，将AI服务实例注册到工厂中
        aiParsingService.registerMeetingServiceWithId(userId, meeting.getId());
        
        // 保存会话历史记录
        aiSessionHistoryService.saveSessionHistory(meeting.getId(), userId, description, 
                meeting.toString(), "CREATE");
        
        return meeting;
    }

    public Meeting updateMeeting(Long id, String updateDescription) {
        String userId = MonitorContextHolder.get().getUserId();
        
        // 使用MyBatis-Flex的查询条件
        Meeting existing = meetingMapper.selectOneById(id);
        if (existing == null) {
            throw new RuntimeException("会议不存在");
        }
        
        String currentTime = "【当前时间】：" + LocalDateTime.now().format(TIME_FORMATTER);
        String updateInstructions = currentTime + "\n【更新描述】：" + updateDescription;

        // 直接获取更新后的会议信息，避免二次调用
        Meeting updated = aiParsingService.updateMeetingDescription(userId, id, existing, updateInstructions);
        
        updated.setId(id);
        meetingMapper.update(updated);  // MyBatis-Flex的更新方法
        
        // 保存会话历史记录
        aiSessionHistoryService.saveSessionHistory(id, userId, updateDescription, 
                updated.toString(), "UPDATE");
        
        return updated;
    }

    public void deleteMeeting(Long id) {
        String userId = MonitorContextHolder.get().getUserId();
        Meeting meeting = meetingMapper.selectOneById(id);
        if (meeting == null) {
            throw new RuntimeException("会议不存在");
        }

        // AI确认删除意图
        String confirmation = aiParsingService.confirmDeletion(userId, id, meeting.getTitle());
        if (confirmation.contains("确认") || confirmation.contains("是")) {
            meetingMapper.deleteById(id);  // MyBatis-Flex的删除方法
            
            // 删除会话历史记录
            aiSessionHistoryService.deleteSessionHistoryByMeetingId(id);
            
            // 清除缓存
            aiParsingService.clearCache(userId, id);
        } else {
            throw new RuntimeException("AI确认删除失败: " + confirmation);
        }
    }

    public Meeting getMeeting(Long id) {
        return meetingMapper.selectOneById(id);  // MyBatis-Flex的查询方法
    }

    public List<Meeting> getAllMeetings() {
        // 使用MyBatis-Flex的查询构造器进行条件查询和排序
        QueryWrapper queryWrapper = QueryWrapper.create()
                .orderBy(MeetingTableDef.MEETING.MEETING_TIME, false); // 按会议时间降序
        return meetingMapper.selectListByQuery(queryWrapper);
    }
}