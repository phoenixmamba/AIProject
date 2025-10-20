package com.cuijian.mymcp.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ContextResponse {
    private String version = "1.0";
    private List<ContextSource> sources;
    private Map<String, Object> data;
    private String status; // "success", "error"
    private String message;
}