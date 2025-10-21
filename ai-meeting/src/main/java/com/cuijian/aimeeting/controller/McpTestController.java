package com.cuijian.aimeeting.controller;

import com.cuijian.aimeeting.service.McpTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * MCP测试控制器
 * 提供前端调用接口，用于测试AI调用MCP服务
 */
@RestController
@RequestMapping("/api/mcp-test")
@CrossOrigin(origins = "http://localhost:8081")
@RequiredArgsConstructor
@Slf4j
public class McpTestController {

    private final McpTestService mcpTestService;

    /**
     * 测试AI调用MCP服务 - 查询用户信息
     * @param userId 用户ID
     * @return AI生成的结果流
     */
    @PostMapping("/query-user")
    public Flux<String> testQueryUser(@RequestParam String userId) {
        log.info("收到测试查询用户信息请求，用户ID: {}", userId);
        return mcpTestService.testQueryUser(userId);
    }

    /**
     * 测试AI调用MCP服务 - 查询产品列表
     * @return AI生成的结果流
     */
    @PostMapping("/query-products")
    public Flux<String> testQueryProducts() {
        log.info("收到测试查询产品列表请求");
        return mcpTestService.testQueryProducts();
    }

    /**
     * 测试AI调用MCP服务 - 自定义查询
     * @param queryType 查询类型
     * @param queryParam 查询参数
     * @return AI生成的结果流
     */
    @PostMapping("/custom-query")
    public Flux<String> testCustomQuery(@RequestParam String queryType, 
                                       @RequestParam String queryParam) {
        log.info("收到自定义查询请求，查询类型: {}，查询参数: {}", queryType, queryParam);
        return mcpTestService.testCustomQuery(queryType, queryParam);
    }
}