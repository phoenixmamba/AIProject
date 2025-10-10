package com.cuijian.aimeeting.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * 简单文件生成AI服务接口
 */
public interface SimpleFileGenerationAiService {
    
    @SystemMessage("你是一个文件生成助手，能够根据用户需求生成简单的文本文件。" +
            "你必须通过使用【写入简单文件】工具来创建文件。" +
            "文件内容应该简洁明了，符合用户需求。")
    String generateSimpleFiles(@UserMessage String userDescription);
}
