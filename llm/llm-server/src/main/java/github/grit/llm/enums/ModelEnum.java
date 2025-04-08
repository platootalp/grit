package github.grit.llm.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ModelEnum {

	QWEN_TURBO("qwen-turbo","text"),
	DEEPSEEK_V3("deepseek-v3", "text"),
	DEEPSEEK_R1("deepseek-r1", "reasoning"),

	TEXT_EMBEDDING_V2("text-embedding-v2","embedding"),
	TEXT_EMBEDDING_V3("text-embedding-v3","embedding"),

	JINA_RERANKER_V2_BASE_MULTILINGUAL("jina-reranker-v2-base-multilingual","scoring")
	;
	private final String name;
	private final String type;
}
