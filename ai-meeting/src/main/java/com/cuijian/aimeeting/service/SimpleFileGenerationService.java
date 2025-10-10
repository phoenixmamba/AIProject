package com.cuijian.aimeeting.service;

import com.cuijian.aimeeting.ai.model.message.AiResponseMessage;
import com.cuijian.aimeeting.ai.model.message.ToolExecutedMessage;
import com.cuijian.aimeeting.ai.model.message.ToolRequestMessage;
import com.cuijian.aimeeting.ai.utils.SimpleFileWriteTool;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolExecution;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 简单文件生成服务类
 */
@Slf4j
@Service
public class SimpleFileGenerationService {
    @Resource(name = "openAiChatModel")
    private ChatModel chatModel;

    @Resource
    private SimpleFileWriteTool simpleFileWriteTool;

//    public SimpleFileGenerationService(
//                                       SimpleFileWriteTool simpleFileWriteTool) {
//        this.simpleFileWriteTool = simpleFileWriteTool;
//
//        // 创建AI服务实例并注册工具
//
//    }

    /**
     * 根据用户描述生成简单文件
     * @param description 用户描述
     * @return 生成结果
     */
    public Flux<String> generateSimpleFiles(String description) {
        try {
            String currentTime = "【当前时间】：" + java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String fullContext = currentTime + "\n【用户需求】：" + description;
            SimpleFileGenerationAiService simpleFileGenerationAiService = AiServices.builder(SimpleFileGenerationAiService.class).chatModel(chatModel)
                    .tools(simpleFileWriteTool).build();
            // 调用AI生成文件
            String response = simpleFileGenerationAiService.generateSimpleFiles(fullContext);
            // 将响应包装成 Flux 流
            return Flux.create(sink -> {
                // 发送AI响应消息
                AiResponseMessage aiResponseMessage = new AiResponseMessage(response);
                sink.next(JSONUtil.toJsonStr(aiResponseMessage));

                // 发送完成信号
                sink.complete();
            });
        } catch (Exception e) {
            log.error("简单文件生成失败", e);
            return Flux.error(e);
        }
    }
}
