package com.cuijian.aimeeting.controller;

import cn.hutool.json.JSONUtil;
import com.cuijian.aimeeting.service.SimpleFileGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 文件生成控制器
 */
@RestController
@RequestMapping("/api/file-generation")
@RequiredArgsConstructor
public class FileGenerationController {

    @Autowired
    private SimpleFileGenerationService simpleFileGenerationService;

    /**
     * 根据用户描述生成简单文本文件
     * @param description 用户描述内容
     * @return 生成结果
     */
    @PostMapping("/generateSimpleTxt")
    public Flux<ServerSentEvent<String>> generateSimpleFiles(
            @RequestBody String description) {
        Flux<String> contentFlux = simpleFileGenerationService.generateSimpleFiles(description);
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
}
