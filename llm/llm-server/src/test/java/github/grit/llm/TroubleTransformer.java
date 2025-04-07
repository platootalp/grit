package github.grit.llm;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentTransformer;
import dev.langchain4j.data.document.Metadata;
import lombok.Builder;

@Builder
public class TroubleTransformer implements DocumentTransformer {

	@Override
	public Document transform(Document document) {
		Metadata metadata = document.metadata();
		metadata.put("trouble","you have trouble");
		return document;
	}
}
