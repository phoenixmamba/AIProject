package com.cuijian.aimeeting.controller;

import cn.hutool.json.JSONUtil;
import com.cuijian.aimeeting.service.NovelGenerateService;
import com.cuijian.aimeeting.service.SimpleFileGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 小说生成控制器
 */
@RestController
@CrossOrigin(origins = "http://localhost:8081")
@RequestMapping("/api/novel")
@RequiredArgsConstructor
public class NovelGenerateController {

    @Autowired
    private NovelGenerateService novelGenerateService;

    /**
     * 根据用户描述生成简单文本文件
     * @param description 用户描述内容
     * @return 生成结果
     */
    @PostMapping("/generateSimpleNovel")
    public Flux<ServerSentEvent<String>> generateSimpleNovel(
            @RequestBody String description) {
        Flux<String> contentFlux = novelGenerateService.generateSimpleNovel(description);
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
