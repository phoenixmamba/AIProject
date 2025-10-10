package com.cuijian.aimeeting.service;

import com.cuijian.aimeeting.utils.SpringContextUtil;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * 简单文件生成服务类
 */
@Slf4j
@Service
public class NovelGenerateService {
    @Resource(name = "openAiChatModel")
    private ChatModel chatModel;

    /**
     * 根据用户描述生成简单文件
     *
     * @param description 用户描述
     * @return 生成结果
     */
    public Flux<String> generateSimpleNovel(String description) {
        try {
            StreamingChatModel openAiStreamingChatModel = SpringContextUtil.getBean("streamingChatModelPrototype", StreamingChatModel.class);
            NovelGenerateAiService novelGenerateAiService = AiServices.builder(NovelGenerateAiService.class)
                    .chatModel(chatModel)
                    .streamingChatModel(openAiStreamingChatModel)
                    .build();
            // 调用AI生成文件
            Flux<String> response = novelGenerateAiService.generateSimpleFiles(description);
            // 解析并处理返回的数据格式
            return response.map(data -> data);
        } catch (Exception e) {
            log.error("简单文件生成失败", e);
            return Flux.error(e);
        }
    }


}
