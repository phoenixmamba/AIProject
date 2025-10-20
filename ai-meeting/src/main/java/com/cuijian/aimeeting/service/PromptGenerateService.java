package com.cuijian.aimeeting.service;

import dev.langchain4j.service.SystemMessage;
import reactor.core.publisher.Flux;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/9/26 10:46
 **/

public interface PromptGenerateService {

    /**
     * 提示词优化
     *
     * @param userMessage 用户提示词
     * @return AI 的输出结果
     */
    @SystemMessage(fromResource = "prompt/prompt-master.txt")
    Flux<String> generatePrompt(String userMessage);
}