package com.cuijian.aimeeting.ai.tools;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

@Data
public abstract class McpBaseTool {

    @Description("工具的英文名称，用于代码中引用")
    private String toolName;

    @Description("工具的显示名称，用于前端展示")
    private String displayName;

    @Description("工具的描述信息")
    private String description;

    @JsonIgnore
    private ToolSpecification toolSpecification;

    public abstract String generateToolExecutedResult(JSONObject arguments);
}