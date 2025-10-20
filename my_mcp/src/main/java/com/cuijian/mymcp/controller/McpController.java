package com.cuijian.mymcp.controller;

import com.cuijian.mymcp.model.ContextRequest;
import com.cuijian.mymcp.model.ContextResponse;
import com.cuijian.mymcp.service.McpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mcp")
@CrossOrigin(origins = "*")
public class McpController {

    @Autowired
    private McpService mcpService;

    @PostMapping("/query")
    public ContextResponse query(@RequestBody ContextRequest request) {
        try {
            // 处理MCP请求并返回结果
            return mcpService.processRequest(request);
        } catch (Exception e) {
            ContextResponse errorResponse = new ContextResponse();
            errorResponse.setStatus("error");
            errorResponse.setMessage("Error processing request: " + e.getMessage());
            return errorResponse;
        }
    }
}