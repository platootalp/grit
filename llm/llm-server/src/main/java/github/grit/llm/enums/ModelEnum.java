package github.grit.llm.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ModelEnum {

	DEEPSEEK_V3("deepseek-v3", "text"),
	DEEPSEEK_R1("deepseek-r1", "reasoning"),
	TEXT_EMBEDDING_V3("text-embedding-v3","embedding"),

	;
	private final String name;
	private final String type;
}
