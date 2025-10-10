package com.cuijian.aimeeting.service;

import com.cuijian.aimeeting.mapper.MeetingMapper;
import com.cuijian.aimeeting.entity.Meeting;
import com.cuijian.aimeeting.entity.table.MeetingTableDef;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Meeting createMeetingByDescription(String description) {
        // 通过AI解析用户描述生成会议对象
        String currentTime = "【当前时间】：" + LocalDateTime.now().format(TIME_FORMATTER);
        String fullContext = currentTime + "\n【会议描述】：" + description;
        Meeting meeting = aiParsingService.parseMeetingDescription(fullContext);
        meetingMapper.insert(meeting);  // MyBatis-Flex的插入方法
        return meeting;
    }

    public Meeting updateMeeting(Long id, String updateDescription) {
        // 使用MyBatis-Flex的查询条件
        Meeting existing = meetingMapper.selectOneById(id);
        if (existing == null) {
            throw new RuntimeException("会议不存在");
        }
        String currentTime = "【当前时间】：" + LocalDateTime.now().format(TIME_FORMATTER);
        // 生成更新指令并解析
        String updateInstructions = aiParsingService.generateUpdateInstructions(
                existing.toString(), currentTime+"\n【更新描述】："+updateDescription);

        Meeting updated = aiParsingService.parseMeetingDescription(updateInstructions);
        updated.setId(id);
        meetingMapper.update(updated);  // MyBatis-Flex的更新方法
        return updated;
    }

    public void deleteMeeting(Long id) {
        Meeting meeting = meetingMapper.selectOneById(id);
        if (meeting == null) {
            throw new RuntimeException("会议不存在");
        }

        // AI确认删除意图
        String confirmation = aiParsingService.confirmDeletion(meeting.getTitle());
        if (confirmation.contains("确认") || confirmation.contains("是")) {
            meetingMapper.deleteById(id);  // MyBatis-Flex的删除方法
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
