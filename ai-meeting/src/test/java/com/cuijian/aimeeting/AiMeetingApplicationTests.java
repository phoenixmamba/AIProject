package com.cuijian.aimeeting;

        import dev.langchain4j.data.message.AiMessage;
        import dev.langchain4j.data.message.SystemMessage;
        import dev.langchain4j.data.message.UserMessage;
        import dev.langchain4j.model.openai.OpenAiChatModel;
        import org.junit.jupiter.api.Test;
        import org.springframework.beans.factory.annotation.Value;
        import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiMeetingApplicationTests {
    @Value("${langchain4j.open-ai.chat-model.api-key}")
    private String apiKey;

    @Value("${langchain4j.open-ai.chat-model.model-name}")
    private String modelName;

    @Value("${langchain4j.open-ai.chat-model.base-url}")
    private String baseUrl;

    @Test
    void contextLoads() {
        OpenAiChatModel aiChatModel =OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .baseUrl(baseUrl)
                .temperature(0.3)  // 控制生成内容的随机性
                .maxTokens(1024)   // 最大生成 tokens
                .build();
        UserMessage firstUserMessage = UserMessage.from("你好，你的名字是什么");
        SystemMessage systemMessage = SystemMessage.from("你是一个智能助手,你的名字是小A");
        AiMessage firstAiMessage = aiChatModel.chat(systemMessage,firstUserMessage).aiMessage();
        System.out.println(firstAiMessage.text());
    }

}
