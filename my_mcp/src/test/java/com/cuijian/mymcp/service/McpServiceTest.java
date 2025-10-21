package com.cuijian.mymcp.service;

import com.cuijian.mymcp.model.ContextRequest;
import com.cuijian.mymcp.model.ContextResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class McpServiceTest {

    private McpService mcpService;

    @BeforeEach
    void setUp() {
        mcpService = new McpService();
    }

    @Test
    void testProcessRequestWithGetUserById() {
        // 准备测试数据
        ContextRequest request = new ContextRequest();
        Map<String, Object> query = new HashMap<>();
        query.put("getUserById", "123");
        request.setQuery(query);

        // 执行测试
        ContextResponse response = mcpService.processRequest(request);

        // 验证结果
        assertEquals("1.0", response.getVersion());
        assertEquals("success", response.getStatus());
        assertNotNull(response.getData());
        assertTrue(response.getData().containsKey("user"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) response.getData().get("user");
        assertEquals("123", user.get("id"));
        assertEquals("John Doe", user.get("name"));
    }

    @Test
    void testProcessRequestWithGetProductList() {
        // 准备测试数据
        ContextRequest request = new ContextRequest();
        Map<String, Object> query = new HashMap<>();
        query.put("getProductList", true);
        request.setQuery(query);

        // 执行测试
        ContextResponse response = mcpService.processRequest(request);

        // 验证结果
        assertEquals("1.0", response.getVersion());
        assertEquals("success", response.getStatus());
        assertNotNull(response.getData());
        assertTrue(response.getData().containsKey("products"));
        
        @SuppressWarnings("unchecked")
        Iterable<Map<String, Object>> products = (Iterable<Map<String, Object>>) response.getData().get("products");
        assertNotNull(products);
    }

    @Test
    void testProcessRequestWithoutQuery() {
        // 准备测试数据
        ContextRequest request = new ContextRequest();

        // 执行测试
        ContextResponse response = mcpService.processRequest(request);

        // 验证结果
        assertEquals("1.0", response.getVersion());
        assertEquals("success", response.getStatus());
        assertNotNull(response.getData());
        assertTrue(response.getData().containsKey("message"));
    }
}