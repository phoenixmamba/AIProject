package com.cuijian.aimeeting.entity;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class McpQueryResponse {
    private String version;
    private List<McpQueryRequest.ContextSource> sources;
    private Map<String, Object> data;
    private String status;
    private String message;
}