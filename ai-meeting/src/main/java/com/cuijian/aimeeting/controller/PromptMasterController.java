package com.cuijian.aimeeting.controller;

import cn.hutool.json.JSONUtil;
import com.cuijian.aimeeting.service.NovelGenerateService;
import com.cuijian.aimeeting.service.PromptMasterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 提示词助手
 */
@RestController
@RequestMapping("/api/prompt")
@RequiredArgsConstructor
@Slf4j
public class PromptMasterController {

    @Autowired
    private PromptMasterService promptMasterService;

    /**
     * 新的对话
     *
     *
     * @return 生成结果
     */
    @GetMapping("/newConversation/{id}")
    public Flux<ServerSentEvent<String>> newConversation(HttpServletRequest request,
                                                         @PathVariable String id) {
        String userId = request.getHeader("userId") == null ? "admin" : request.getHeader("userId");

        Flux<String> contentFlux = promptMasterService.generatePrompt(userId,id,"初始化");

        return contentFlux
                .map(chunk -> {
                    return ServerSentEvent.<String>builder()
                            .data(Map.of("d", chunk).get("d"))
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
     * 根据用户描述生成并优化提示词
     *
     *
     * @return 生成结果
     */
    @GetMapping("/generatePrompt/{id}")
    public Flux<ServerSentEvent<String>> generatePrompt(HttpServletRequest request,
                                                        @PathVariable String id,
                                                        @RequestParam String description) {
        String userId = request.getHeader("userId") == null ? "admin" : request.getHeader("userId");
        
        Flux<String> contentFlux = promptMasterService.generatePrompt(userId,id,description);
        
        // 收集完整的返回内容
        StringBuilder fullResponse = new StringBuilder();

        return contentFlux
                .map(chunk -> {
                    return ServerSentEvent.<String>builder()
                            .data(Map.of("d", chunk).get("d"))
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
}
