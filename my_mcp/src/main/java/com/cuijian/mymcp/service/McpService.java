package com.cuijian.mymcp.service;

import com.cuijian.mymcp.model.ContextRequest;
import com.cuijian.mymcp.model.ContextResponse;
import com.cuijian.mymcp.model.ContextSource;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class McpService {
    
    public ContextResponse processRequest(ContextRequest request) {
        ContextResponse response = new ContextResponse();
        response.setVersion("1.0");
        response.setStatus("success");
        response.setMessage("Request processed successfully");
        
        // 设置sources信息
        List<ContextSource> sources = new ArrayList<>();
        if (request.getSources() != null) {
            sources.addAll(request.getSources());
        }
        response.setSources(sources);
        
        // 创建模拟数据
        Map<String, Object> data = new HashMap<>();
        
        // 这里应该根据实际的数据源实现查询逻辑
        // 目前我们只是示例，所以返回一些模拟数据
        if (request.getQuery() != null) {
            if (request.getQuery().containsKey("getUserById")) {
                // 模拟查询用户信息
                Map<String, Object> user = new HashMap<>();
                user.put("id", request.getQuery().get("getUserById"));
                user.put("name", "John Doe");
                user.put("email", "john.doe@example.com");
                data.put("user", user);
            } else if (request.getQuery().containsKey("getProductList")) {
                // 模拟查询产品列表
                List<Map<String, Object>> products = new ArrayList<>();
                Map<String, Object> product1 = new HashMap<>();
                product1.put("id", 1);
                product1.put("name", "Product 1");
                product1.put("price", 99.99);
                
                Map<String, Object> product2 = new HashMap<>();
                product2.put("id", 2);
                product2.put("name", "Product 2");
                product2.put("price", 149.99);
                
                products.add(product1);
                products.add(product2);
                data.put("products", products);
            } else {
                // 默认返回一些通用数据
                data.put("message", "No specific query provided. This is sample data.");
                data.put("timestamp", System.currentTimeMillis());
            }
        } else {
            // 默认返回一些通用数据
            data.put("message", "No query provided. This is sample data.");
            data.put("timestamp", System.currentTimeMillis());
        }
        
        response.setData(data);
        return response;
    }
}