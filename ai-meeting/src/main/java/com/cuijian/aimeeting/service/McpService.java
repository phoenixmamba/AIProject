package com.cuijian.aimeeting.service;

import com.cuijian.aimeeting.entity.McpQueryRequest;
import com.cuijian.aimeeting.entity.McpQueryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class McpService {
    
    /**
     * 处理MCP查询请求
     * @param request 查询请求
     * @return 查询响应
     */
    public McpQueryResponse handleQuery(McpQueryRequest request) {
        McpQueryResponse response = new McpQueryResponse();
        response.setVersion(request.getVersion() != null ? request.getVersion() : "1.0");
        response.setSources(request.getSources());
        
        Map<String, Object> responseData = new HashMap<>();
        
        if (request.getQuery() != null && !request.getQuery().isEmpty()) {
            // 处理特定查询
            if (request.getQuery().containsKey("getUserById")) {
                // 根据用户ID查询用户信息
                String userId = (String) request.getQuery().get("getUserById");
                Map<String, Object> user = new HashMap<>();
                user.put("id", userId);
                user.put("name", "John Doe");
                user.put("email", "john.doe@example.com");
                
                responseData.put("user", user);
                response.setStatus("success");
                response.setMessage("Request processed successfully");
            } else if (request.getQuery().containsKey("getProductList")) {
                // 查询产品列表
                Boolean getProductList = (Boolean) request.getQuery().get("getProductList");
                if (getProductList != null && getProductList) {
                    List<Map<String, Object>> products = new ArrayList<>();
                    
                    Map<String, Object> product1 = new HashMap<>();
                    product1.put("id", 1);
                    product1.put("name", "Product 1");
                    product1.put("price", 99.99);
                    products.add(product1);
                    
                    Map<String, Object> product2 = new HashMap<>();
                    product2.put("id", 2);
                    product2.put("name", "Product 2");
                    product2.put("price", 149.99);
                    products.add(product2);
                    
                    responseData.put("products", products);
                    response.setStatus("success");
                    response.setMessage("Request processed successfully");
                } else {
                    response.setStatus("error");
                    response.setMessage("Invalid getProductList parameter");
                }
            } else {
                response.setStatus("error");
                response.setMessage("Unsupported query type");
            }
        } else {
            // 无特定查询，返回示例数据
            responseData.put("message", "No specific query provided. This is sample data.");
            responseData.put("timestamp", System.currentTimeMillis());
            response.setStatus("success");
            response.setMessage("Request processed successfully");
        }
        
        response.setData(responseData);
        return response;
    }
}