package com.cuijian.aimeeting.ai.utils;

import cn.hutool.json.JSONObject;
import com.cuijian.aimeeting.ai.tools.BaseTool;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 简单文件写入工具
 * 用于测试AI调用本地工具的基本功能
 */
@Slf4j
@Component
public class SimpleFileWriteTool extends BaseTool {

    @Tool("写入简单的文本文件到指定路径")
    public String writeSimpleFile(
            @P("文件的相对路径")
            String relativeFilePath,
            @P("要写入文件的内容")
            String content
    ) {
        try {
            Path path = Paths.get(relativeFilePath);
            if (!path.isAbsolute()) {
                // 相对路径处理，创建基于 appId 的项目目录
                String projectDirName = "simpleTxt";
                Path projectRoot = Paths.get(System.getProperty("user.dir") + "/tmp/simple_output", projectDirName);
                path = projectRoot.resolve(relativeFilePath);
            }
            // 创建父目录（如果不存在）
            Path parentDir = path.getParent();
            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }
            // 写入文件内容
            Files.write(path, content.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            log.info("成功写入文件: {}", path.toAbsolutePath());
            // 返回相对路径
            return "文件写入成功: " + relativeFilePath;
        } catch (IOException e) {
            String errorMessage = "文件写入失败: " + relativeFilePath + ", 错误: " + e.getMessage();
            log.error(errorMessage, e);
            return errorMessage;
        }
    }

    @Override
    public String getToolName() {
        return "writeSimpleFile";
    }

    @Override
    public String getDisplayName() {
        return "写入简单文件";
    }

    @Override
    public String generateToolExecutedResult(JSONObject arguments) {
        String relativeFilePath = arguments.getStr("relativeFilePath");
        String content = arguments.getStr("content");
        return String.format("""
                        [工具调用] %s %s
                                                """, getDisplayName(), relativeFilePath, content);
    }
}