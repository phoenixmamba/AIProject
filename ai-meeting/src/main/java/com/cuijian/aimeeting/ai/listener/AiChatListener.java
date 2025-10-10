package com.cuijian.aimeeting.ai.listener;

import com.cuijian.aimeeting.monitor.MonitorContextHolder;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import dev.langchain4j.service.AiServices;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/9/30 13:54
 **/
@Component
public class AiChatListener implements ChatModelListener {
    @Override
    public void onRequest(ChatModelRequestContext requestContext){
        System.out.println("当前请求用户：" + MonitorContextHolder.get().getUserId() + 
                          "，会议ID：" + MonitorContextHolder.get().getMeetingId());
        
        // 打印ChatMemory中的内容
        printChatMemoryContents();
    }

    @Override
    public void onResponse(ChatModelResponseContext responseContext) {
        System.out.println("当前接受返回用户：" + MonitorContextHolder.get().getUserId() + 
                          "，会议ID：" + MonitorContextHolder.get().getMeetingId());
    }

    @Override
    public void onError(ChatModelErrorContext errorContext) {
        System.out.println("AI调用错误，用户：" + MonitorContextHolder.get().getUserId() + 
                          "，会议ID：" + MonitorContextHolder.get().getMeetingId());
    }
    
    /**
     * 打印ChatMemory中的内容
     */
    private void printChatMemoryContents() {
        try {
            // 通过反射获取当前线程的AiService上下文
            // 注意：这种方法依赖于Langchain4j的内部实现，可能在不同版本中有所不同
            System.out.println("=== ChatMemory内容开始 ===");
            // 由于我们无法直接访问ChatMemory实例，我们在AiServices中添加一些调试信息
            System.out.println("注意：直接访问ChatMemory需要修改AiServices的使用方式");
            System.out.println("=== ChatMemory内容结束 ===");
        } catch (Exception e) {
            System.out.println("无法获取ChatMemory内容: " + e.getMessage());
        }
    }
}