package github.grit.llm.service;


import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;

public interface Copilot {

    @SystemMessage("You are a good friend of mine. Answer using slang.")
    String chat(String userMessage);

    TokenStream streamChat(String message);
}
