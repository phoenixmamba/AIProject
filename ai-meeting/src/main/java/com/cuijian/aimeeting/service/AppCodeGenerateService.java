package com.cuijian.aimeeting.service;

import com.cuijian.aimeeting.entity.AiSessionHistory;
import com.cuijian.aimeeting.entity.Meeting;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.SystemMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/9/26 10:46
 **/

public interface AppCodeGenerateService {

    /**
     * 生成多文件代码
     *
     * @param userMessage 用户提示词
     * @return AI 的输出结果
     */
    @SystemMessage(fromResource = "prompt/multi-file-system-prompt.txt")
    Flux<String> generateMultiFileCodeStream(String userMessage);
}