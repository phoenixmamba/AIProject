package com.cuijian.aimeeting.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

/**
 * 小说生成AI服务接口
 */
public interface NovelGenerateAiService {
    
    @SystemMessage(fromResource = "prompt/novel-prompt.txt")
    Flux<String> generateSimpleFiles(@UserMessage String userDescription);
}
