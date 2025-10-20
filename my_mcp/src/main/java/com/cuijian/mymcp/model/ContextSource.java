package com.cuijian.mymcp.model;

import lombok.Data;

@Data
public class ContextSource {
    private String name;
    private String uri;
    private String type; // "database", "api", "file", etc.
    private String description;
}