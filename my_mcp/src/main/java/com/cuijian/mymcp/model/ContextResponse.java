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

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}