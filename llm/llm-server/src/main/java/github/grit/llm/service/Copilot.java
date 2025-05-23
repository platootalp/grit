package github.grit.llm.service;


import dev.langchain4j.service.SystemMessage;
import reactor.core.publisher.Flux;

public interface Copilot {

    @SystemMessage("You are a good friend of mine. Answer using slang.")
    String chat(String userMessage);

    Flux<String> streamChat(String message);

    Long getCalculation(String userMessage);
}
