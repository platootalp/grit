package github.grit.llm.config;

import dev.langchain4j.http.client.spring.restclient.SpringRestClientBuilder;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
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
                    .httpClientBuilder(new SpringRestClientBuilder())
                    .build();
            modelMap.put(name, model);
        });
        return modelMap;
    }

    @Bean
    public Map<String, StreamingChatLanguageModel> streamingModelBeans() {
        Map<String, StreamingChatLanguageModel> modelMap = new HashMap<>();
        modelProperties.getStreamModels().forEach((name, properties) -> {
            StreamingChatLanguageModel model = OpenAiStreamingChatModel.builder()
                    .apiKey(properties.getApiKey())
                    .modelName(properties.getModelName())
                    .baseUrl(properties.getBaseUrl())
                    .httpClientBuilder(new SpringRestClientBuilder())
                    .build();
            modelMap.put(name, model);
        });
        return modelMap;
    }

    @Bean
    public Map<String, EmbeddingModel> embeddingModelMap(){
        Map<String, EmbeddingModel> modelMap = new HashMap<>();
        modelProperties.getEmbeddings().forEach((name, properties) -> {
            EmbeddingModel model = OpenAiEmbeddingModel.builder()
                    .apiKey(properties.getApiKey())
                    .modelName(properties.getModelName())
                    .baseUrl(properties.getBaseUrl())
                    .httpClientBuilder(new SpringRestClientBuilder())
                    .build();
            modelMap.put(name, model);
        });
        return modelMap;
    }
}

