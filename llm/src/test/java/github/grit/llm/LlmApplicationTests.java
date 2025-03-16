package github.grit.llm;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
class LlmApplicationTests {

    @Autowired
    private Map<String, ChatLanguageModel> modelBeans;

    @Test
    void contextLoads() {
    }

    @Test
    public void testChat(){
        ChatLanguageModel model = modelBeans.get("deepseek-v3");
        // 使用模型
        System.out.println(model.chat("你是誰"));
    }
}
