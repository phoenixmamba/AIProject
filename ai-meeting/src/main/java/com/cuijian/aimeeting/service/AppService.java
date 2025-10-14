package com.cuijian.aimeeting.service;

import cn.hutool.core.util.StrUtil;
import com.cuijian.aimeeting.ai.AppAiServiceFactory;
import com.cuijian.aimeeting.entity.AppChatHistory;
import com.cuijian.aimeeting.entity.AppInfo;
import com.cuijian.aimeeting.mapper.AppInfoMapper;
import com.cuijian.aimeeting.mapper.ChatHistoryMapper;
import com.cuijian.aimeeting.monitor.MonitorContext;
import com.cuijian.aimeeting.monitor.MonitorContextHolder;
import com.cuijian.aimeeting.parser.CodeParserExecutor;
import com.cuijian.aimeeting.saver.CodeFileSaverExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/10/14 10:23
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class AppService {
    private final AppInfoMapper appInfoMapper;
    private final ChatHistoryMapper chatHistoryMapper;
    private final AppAiServiceFactory appAiServiceFactory;

    // 用于存储创建会议时的临时AI服务实例
    private final ThreadLocal<AppAiServiceFactory.NewServiceInstance> tempServiceInstance = new ThreadLocal<>();


    /**
     * 生成应用代码代码
     *
     * @param appId   应用 ID
     * @param message 用户输入
     * @return 生成的代码
     */
    public Flux<String> chatToGenCode(String appId, String message,String userId) {
        // 参数校验
        if (appId == null || StrUtil.isBlank(message)) {
            throw new IllegalArgumentException("应用ID和消息内容不能为空");
        }

        // 查询应用信息
        AppInfo appInfo = appInfoMapper.selectOneById(appId);
        if (appInfo == null) {
            throw new IllegalArgumentException("应用不存在，ID: " + appId);
        }

        // 在调用 AI 前，先保存用户消息到数据库中
        AppChatHistory userChatHistory = new AppChatHistory();
        userChatHistory.setMessage(message);
        userChatHistory.setMessageType("user");
        userChatHistory.setAppId(appId);
        userChatHistory.setUserId(userId);
        chatHistoryMapper.insertSelective(userChatHistory);

        //  设置监控上下文（用户 ID 和应用 ID）
        MonitorContextHolder.set(MonitorContext.builder()
                .userId(userId)
                .appId(appId)
                .build());

        //  调用 AI 生成代码（流式）
        AppCodeGenerateService appCodeGenerateService= appAiServiceFactory.getAppCodeGenerateService(userId, appId);
        Flux<String> aiResponse = appCodeGenerateService.generateMultiFileCodeStream(message);
        aiResponse = processCodeStream(aiResponse, appId);
        // 8. 收集 AI 响应的内容，并且在完成后保存记录到对话历史
        StringBuilder aiMessageBuilder = new StringBuilder();
        return aiResponse.doOnNext(aiMessageBuilder::append)
                .doOnComplete(() -> {
                    // 保存AI响应到数据库
                    AppChatHistory aiChatHistory = new AppChatHistory();
                    aiChatHistory.setMessage(aiMessageBuilder.toString());
                    aiChatHistory.setMessageType("ai");
                    aiChatHistory.setAppId(appId);
                    aiChatHistory.setUserId(userId);
                    chatHistoryMapper.insertSelective(aiChatHistory);
                });
    }


    public String createApp(String appName, String initPrompt, String userId) {

        // 构造入库对象
        AppInfo app = new AppInfo();
        app.setAppName(appName);
        app.setInitPrompt(initPrompt);
        app.setUserId(userId);
//        app.setCodeGenType(selectedCodeGenType.getValue());
        // 插入数据库
        appInfoMapper.insertSelective(app);
        log.info("应用创建成功，ID: {}", app.getId());
        return app.getId();
    }

    /**
     * 根据ID获取应用信息
     *
     * @param id 应用ID
     * @return 应用信息
     */
    public AppInfo getAppInfoById(String id) {
        if (StrUtil.isBlank(id)) {
            throw new IllegalArgumentException("应用ID不能为空");
        }
        return appInfoMapper.selectOneById(id);
    }

    /**
     * 通用流式代码处理方法
     *
     * @param codeStream  代码流
     * @param appId       应用 ID
     * @return 流式响应
     */
    private Flux<String> processCodeStream(Flux<String> codeStream, String appId) {
        // 字符串拼接器，用于当流式返回所有的代码之后，再保存代码
        StringBuilder codeBuilder = new StringBuilder();
        return codeStream.doOnNext(chunk -> {
            // 实时收集代码片段
            codeBuilder.append(chunk);
        }).doOnComplete(() -> {
            // 流式返回完成后，保存代码
            try {
                String completeCode = codeBuilder.toString();
                // 使用执行器解析代码
                Object parsedResult = CodeParserExecutor.executeParser(completeCode);
                // 使用执行器保存代码
                File saveDir = CodeFileSaverExecutor.executeSaver(parsedResult, appId);
                log.info("保存成功，目录为：{}", saveDir.getAbsolutePath());
            } catch (Exception e) {
                log.error("保存失败: {}", e.getMessage());
            }
        });
    }
}