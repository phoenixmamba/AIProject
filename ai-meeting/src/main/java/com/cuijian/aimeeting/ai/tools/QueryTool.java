package com.cuijian.aimeeting.ai.tools;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cuijian.aimeeting.entity.McpQueryRequest;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * MCP查询工具
 * 允许AI通过工具调用的方式查询上下文信息
 */
@Slf4j
@Component
public class QueryTool extends BaseTool {
    
    public QueryTool() {
        setToolName("mcp_query");
        setDisplayName("MCP查询工具");
        setDescription("查询上下文信息，支持用户信息查询和产品列表查询");
    }

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Tool("查询上下文信息")
    public String queryContext(
            @P("查询类型，例如getUserById或getProductList")
            String queryType,
            @P("查询参数，根据查询类型提供相应的参数")
            String queryParam) {
        
        try {
            // 构造MCP查询请求
            McpQueryRequest request = new McpQueryRequest();
            request.setVersion("1.0");
            
            Map<String, Object> query = new HashMap<>();
            if ("getUserById".equals(queryType)) {
                query.put("getUserById", queryParam);
            } else if ("getProductList".equals(queryType)) {
                query.put("getProductList", Boolean.parseBoolean(queryParam));
            } else {
                query.put(queryType, queryParam);
            }
            request.setQuery(query);
            
            // 发送HTTP请求到MCP服务
            String requestBody = JSONUtil.toJsonStr(request);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8002/ai-mcp/mcp/query"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(30))
                    .build();
            
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            
            log.info("MCP服务调用结果，状态码: {}，响应: {}", httpResponse.statusCode(), httpResponse.body());
            
            if (httpResponse.statusCode() == 200) {
                return httpResponse.body();
            } else {
                return "{\"status\":\"error\",\"message\":\"HTTP " + httpResponse.statusCode() + "\"}";
            }
        } catch (Exception e) {
            log.error("调用MCP服务时发生错误", e);
            return "{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}";
        }
    }

    @Override
    public String generateToolExecutedResult(JSONObject arguments) {
        return null;
    }
}