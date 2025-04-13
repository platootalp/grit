package github.grit.llm.mcp.tool;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.service.AiServices;
import github.grit.llm.service.Copilot;
import github.grit.llm.util.LangChain4jUtil;
import github.grit.llm.util.ToolUtil;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ToolTest {

    @Autowired
    private LangChain4jUtil langChain4jUtil;

    @Test
    public void testTool() {
        ChatLanguageModel chatModel = langChain4jUtil.getChatModel(null);
        ToolUtil toolUtil = new ToolUtil();
        Copilot copilot = AiServices.builder(Copilot.class)
                .chatLanguageModel(chatModel)
                .tools(toolUtil)
                .build();
//        chatModel.chat("请你使用快速幂工具计算2^20的结果");
//        String chat = copilot.chat("请你使用快速幂工具计算2^31的结果");
        // 结构化输出
        Long calculation = copilot.getCalculation("请你使用快速幂工具计算2^31的结果");
        System.out.println(calculation);

    }
}
