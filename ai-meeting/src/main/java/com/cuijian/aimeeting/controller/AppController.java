package com.cuijian.aimeeting.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.cuijian.aimeeting.common.BaseResponse;
import com.cuijian.aimeeting.entity.AppInfo;
import com.cuijian.aimeeting.entity.Meeting;
import com.cuijian.aimeeting.monitor.MonitorContext;
import com.cuijian.aimeeting.monitor.MonitorContextHolder;
import com.cuijian.aimeeting.service.AppService;
import com.cuijian.aimeeting.service.MeetingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 测试ai创建和编辑会议
 * @Date : 2025/9/26 10:52
 **/
@RestController
@RequestMapping("/api/app")
@RequiredArgsConstructor
public class AppController {

    private final AppService appService;

    @PostMapping("/createApp")
    public ResponseEntity<String> createApp(@RequestBody CreateAppRequest request, HttpServletRequest httpServletRequest) {
        String userId = httpServletRequest.getHeader("userId") == null ? "admin" : httpServletRequest.getHeader("userId");
        return ResponseEntity.ok(appService.createApp(request.getAppName(), request.getInitPrompt(), userId));
    }
    @GetMapping(value = "/chat/gen/code", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatToGenCode(@RequestParam String appId,
                                                       @RequestParam String message,
                                                       HttpServletRequest request) {
        // 参数校验
        if (appId == null || StrUtil.isBlank(message)) {
            throw new IllegalArgumentException("应用ID和消息内容不能为空");
        }
        // 获取当前登录用户
        String userId = request.getHeader("userId") == null ? "admin" : request.getHeader("userId");
        // 调用服务生成代码（SSE 流式返回）
        Flux<String> contentFlux = appService.chatToGenCode(appId, message, userId);
        return contentFlux
                .map(chunk -> {
                    Map<String, String> wrapper = Map.of("d", chunk);
                    String jsonData = JSONUtil.toJsonStr(wrapper);
                    return ServerSentEvent.<String>builder()
                            .data(jsonData)
                            .build();
                })
                .concatWith(Mono.just(
                        // 发送结束事件
                        ServerSentEvent.<String>builder()
                                .event("done")
                                .data("")
                                .build()
                ));
    }

    /**
     * 根据 id 获取应用详情
     *
     * @param id 应用 id
     * @return 应用详情
     */
    @GetMapping("/get/vo")
    public BaseResponse<AppInfo> getAppById(String id) {
        // 参数校验
        if (StrUtil.isBlank(id)) {
            throw new IllegalArgumentException("应用ID不能为空");
        }
        
        // 查询应用信息
        AppInfo appInfo = appService.getAppInfoById(id);
        if (appInfo == null) {
            throw new IllegalArgumentException("应用不存在，ID: " + id);
        }
        
        return new BaseResponse<>(0, appInfo, "获取成功");
    }
    @Data
    public static class CreateAppRequest {
        private String appName;
        private String initPrompt;
    }
}