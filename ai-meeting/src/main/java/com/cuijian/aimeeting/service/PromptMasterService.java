package com.cuijian.aimeeting.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.cuijian.aimeeting.ai.AppAiServiceFactory;
import com.cuijian.aimeeting.ai.PromptMasterServiceFactory;
import com.cuijian.aimeeting.entity.AppChatHistory;
import com.cuijian.aimeeting.entity.AppInfo;
import com.cuijian.aimeeting.mapper.AppInfoMapper;
import com.cuijian.aimeeting.mapper.ChatHistoryMapper;
import com.cuijian.aimeeting.monitor.MonitorContext;
import com.cuijian.aimeeting.monitor.MonitorContextHolder;
import com.cuijian.aimeeting.parser.CodeParserExecutor;
import com.cuijian.aimeeting.saver.CodeFileSaverExecutor;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.time.LocalDateTime;

import static com.cuijian.aimeeting.entity.table.ChatHistoryTableDef.CHAT_HISTORY;
import static com.cuijian.aimeeting.entity.table.InfoTableDef.INFO;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/10/14 10:23
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class PromptMasterService {
    private final PromptMasterServiceFactory promptMasterServiceFactory;

    // 用于存储创建会议时的临时AI服务实例
    private final ThreadLocal<PromptMasterServiceFactory.NewServiceInstance> tempServiceInstance = new ThreadLocal<>();



    public Flux<String> generatePrompt(String userId,String id,String userMessage) {
        PromptGenerateService promptGenerateService= promptMasterServiceFactory.getPromptMasterService(userId, id);
        Flux<String> prompt = promptGenerateService.generatePrompt(userMessage);
        StringBuilder codeBuilder = new StringBuilder();
        return prompt.doOnNext(chunk -> {
            // 实时收集代码片段
            codeBuilder.append(chunk);
        });
//        return prompt;
    }

}