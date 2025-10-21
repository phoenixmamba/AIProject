package com.cuijian.aimeeting.service;

import com.cuijian.aimeeting.ai.AppAiServiceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
@RequiredArgsConstructor
public class McpTestService {

    private final AppAiServiceFactory appAiServiceFactory;

    /**
     * 测试AI调用MCP服务 - 查询用户信息
     * @param userId 用户ID
     * @return AI生成的结果流
     */
    public Flux<String> testQueryUser(String userId) {
        // 构造AI提示词，要求AI调用MCP服务查询用户信息
        String userMessage = String.format("请查询用户ID为 %s 的用户信息。请使用MCP工具调用查询用户信息的功能。", userId);
        
        // 获取AI服务实例并生成响应
        McpTestAiService aiService = appAiServiceFactory.getMcpTestAiService("test-user", "mcp-test");
        return aiService.generateResponse(userMessage);
    }

    /**
     * 测试AI调用MCP服务 - 查询产品列表
     * @return AI生成的结果流
     */
    public Flux<String> testQueryProducts() {
        // 构造AI提示词，要求AI调用MCP服务查询产品列表
        String userMessage = "请查询产品列表。请使用MCP工具调用查询产品列表的功能。";
        
        // 获取AI服务实例并生成响应
        McpTestAiService aiService = appAiServiceFactory.getMcpTestAiService("test-user", "mcp-test");
        return aiService.generateResponse(userMessage);
    }

    /**
     * 测试AI调用MCP服务 - 自定义查询
     * @param queryType 查询类型
     * @param queryParam 查询参数
     * @return AI生成的结果流
     */
    public Flux<String> testCustomQuery(String queryType, String queryParam) {
        // 构造AI提示词，要求AI调用MCP服务进行自定义查询
        String userMessage = String.format("请执行一个自定义查询。查询类型：%s，查询参数：%s。请使用MCP工具调用相应的功能。", 
                                          queryType, queryParam);
        
        // 获取AI服务实例并生成响应
        McpTestAiService aiService = appAiServiceFactory.getMcpTestAiService("test-user", "mcp-test");
        return aiService.generateResponse(userMessage);
    }
}