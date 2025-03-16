package github.grit.llm.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class LlmConfig {

    private final ModelProperties modelProperties;

    public LlmConfig(ModelProperties modelProperties) {
        this.modelProperties = modelProperties;
    }

    @Bean
    public Map<String, ChatLanguageModel> modelBeans() {
        Map<String, ChatLanguageModel> modelMap = new HashMap<>();
        modelProperties.getModels().forEach((name, properties) -> {
            ChatLanguageModel model = OpenAiChatModel.builder()
                    .apiKey(properties.getApiKey())
                    .modelName(properties.getModelName())
                    .baseUrl(properties.getBaseUrl())
                    .build();
            modelMap.put(name, model);
        });
        return modelMap;
    }
}

