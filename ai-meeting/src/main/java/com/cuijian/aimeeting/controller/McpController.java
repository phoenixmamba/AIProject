package com.cuijian.aimeeting.controller;

import com.cuijian.aimeeting.entity.McpQueryRequest;
import com.cuijian.aimeeting.entity.McpQueryResponse;
import com.cuijian.aimeeting.service.McpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * MCP服务控制器
 * 提供Model Context Protocol接口供AI调用
 */
@RestController
@RequestMapping("/mcp")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class McpController {

    private final McpService mcpService;

    /**
     * 查询上下文信息
     * @param request 查询请求
     * @return 查询响应
     */
    @PostMapping("/query")
    public McpQueryResponse query(@RequestBody McpQueryRequest request) {
        log.info("收到MCP查询请求: {}", request);
        try {
            McpQueryResponse response = mcpService.handleQuery(request);
            log.info("MCP查询处理完成，状态: {}", response.getStatus());
            return response;
        } catch (Exception e) {
            log.error("处理MCP查询请求时发生错误", e);
            McpQueryResponse errorResponse = new McpQueryResponse();
            errorResponse.setVersion(request.getVersion() != null ? request.getVersion() : "1.0");
            errorResponse.setStatus("error");
            errorResponse.setMessage("Error processing request: " + e.getMessage());
            return errorResponse;
        }
    }
}