package github.grit.llm.controller;

import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import github.grit.llm.service.Copilot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

@RequestMapping("/chat")
@RestController
public class ChatController {

    @Autowired
    private Map<String, ChatLanguageModel> modelBeans;
    @Autowired
    private Map<String, StreamingChatLanguageModel> streamingModelBeans;

    @GetMapping("")
    public String chat(String userMessage) {
        ChatLanguageModel model = modelBeans.get("deepseek-v3");
        Copilot copilot = AiServices.create(Copilot.class, model);

        return copilot.chat(userMessage);
    }

    @GetMapping("/stream")
    public Flux<ServerSentEvent<String>> chat2(String userMessage) {
        StreamingChatLanguageModel streamingChatLanguageModel = streamingModelBeans.get("deepseek-v3");
        Copilot copilot = AiServices.create(Copilot.class, streamingChatLanguageModel);
        return copilot.streamChat(userMessage)
                .doOnNext(message -> System.out.println("message = " + message))
                .map(message -> ServerSentEvent.builder(message).build());
    }

}
