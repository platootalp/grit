package github.grit.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@ConfigurationProperties(prefix = "langchain4j")
@Data
@Component
public class ModelProperties {
    private Map<String, Model> models;

    @Data
    public static class Model {
        private String apiKey;
        private String modelName;
        private String baseUrl;
    }

}
