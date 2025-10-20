package com.cuijian.mymcp.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ContextRequest {
    private String version;
    private List<ContextSource> sources;
    private Map<String, Object> query;
    private Map<String, Object> context;
}