package com.cuijian.aimeeting.ai.listener;

import com.cuijian.aimeeting.monitor.MonitorContextHolder;
import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import org.springframework.stereotype.Component;

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
        System.out.println("Current UserId："+MonitorContextHolder.get().getUserId());

    }

    @Override
    public void onResponse(ChatModelResponseContext responseContext) {
        System.out.println("当前接受返回用户："+MonitorContextHolder.get().getUserId());
    }

    @Override
    public void onError(ChatModelErrorContext errorContext) {

    }

}
