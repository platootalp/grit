package github.grit.llm.spring.tool;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StructuredOutputTest {

    @Test
    public void testStructuredOutput() {
        ChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .baseUrl( "https://dashscope.aliyuncs.com/compatible-mode/v1")
                        .apiKey("sk-f755ab0f995c43ffa206424bb2c43de2")
                        .build())
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("deepseek-v3")
                        .build())
                .build();
        ChatClient chatClient = ChatClient.create(chatModel);
        System.out.println(chatClient.prompt()
                .user("你好")
                .call()
                .content()
        );
    }
}
