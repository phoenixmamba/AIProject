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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<ContextSource> getSources() {
        return sources;
    }

    public void setSources(List<ContextSource> sources) {
        this.sources = sources;
    }

    public Map<String, Object> getQuery() {
        return query;
    }

    public void setQuery(Map<String, Object> query) {
        this.query = query;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }
}