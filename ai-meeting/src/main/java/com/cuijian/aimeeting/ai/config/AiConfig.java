//package com.cuijian.aimeeting.ai.config;
//
//import dev.langchain4j.model.openai.OpenAiChatModel;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.List;
//
///**
// * @version : 1.0
// * @Author : cui_jian
// * @Description :
// * @Date : 2025/9/26 10:31
// **/
//@Configuration
//public class AiConfig {
//
//    @Value("${langchain4j.open-ai.chat-model.api-key}")
//    private String apiKey;
//
//    @Value("${langchain4j.open-ai.chat-model.model-name}")
//    private String modelName;
//
//    @Value("${langchain4j.open-ai.chat-model.base-url}")
//    private String baseUrl;
//
//    @Bean
//    public OpenAiChatModel aiChatModel() {
//        // 配置DeepSeek模型
//        return OpenAiChatModel.builder()
//                .apiKey(apiKey)
//                .modelName(modelName)
//                .baseUrl(baseUrl)
//                .listeners(List.of())
//                .temperature(0.3)  // 控制生成内容的随机性
//                .maxTokens(1024)   // 最大生成 tokens
//                .build();
//    }
//}
