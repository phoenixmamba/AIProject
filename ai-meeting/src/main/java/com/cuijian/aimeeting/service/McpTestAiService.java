package com.cuijian.aimeeting.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

/**
 * MCP测试AI服务接口
 */
public interface McpTestAiService {
    
    @SystemMessage("你是一个AI助手，可以帮助用户查询系统中的各种信息。" +
                   "你可以使用MCP查询工具来获取上下文信息。" +
                   "当用户要求查询信息时，请使用适当的工具来完成查询。" +
                   "查询完成后，请以清晰易懂的方式向用户展示结果。")
    Flux<String> generateResponse(@UserMessage String userMessage);
}