package com.cuijian.aimeeting.controller;

import com.cuijian.aimeeting.entity.Meeting;
import com.cuijian.aimeeting.monitor.MonitorContext;
import com.cuijian.aimeeting.monitor.MonitorContextHolder;
import com.cuijian.aimeeting.service.MeetingService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/9/26 10:52
 **/
@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping("/add")
    public ResponseEntity<Meeting> createMeeting(HttpServletRequest request, @RequestBody String description) {
        String userId = request.getHeader("userId");
        MonitorContextHolder.set(MonitorContext.builder().userId(userId).build());
        return ResponseEntity.ok(meetingService.createMeetingByDescription(description));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Meeting> updateMeeting(
            @PathVariable Long id,
            @RequestBody String updateDescription) {
        return ResponseEntity.ok(meetingService.updateMeeting(id, updateDescription));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeeting(@PathVariable Long id) {
        meetingService.deleteMeeting(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Meeting> getMeeting(@PathVariable Long id) {
        return ResponseEntity.ok(meetingService.getMeeting(id));
    }

    @GetMapping
    public ResponseEntity<List<Meeting>> getAllMeetings() {
        return ResponseEntity.ok(meetingService.getAllMeetings());
    }
}
