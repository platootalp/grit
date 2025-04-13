package github.grit.llm.mcp.tool;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ToolTest {

	@Test
	public void testTool(){
		ToolSpecification toolSpecification = ToolSpecification.builder()
				.name("test")
				.description("test")
				.parameters(JsonObjectSchema.builder()
						.build())
				.build();
	}
}
