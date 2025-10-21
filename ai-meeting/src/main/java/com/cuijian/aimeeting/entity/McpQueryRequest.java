package com.cuijian.aimeeting.entity;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class McpQueryRequest {
    private String version;
    private List<ContextSource> sources;
    private Map<String, Object> query;
    private Map<String, Object> context;
    
    @Data
    public static class ContextSource {
        private String name;
        private String uri;
        private String type;
        private String description;
    }
}