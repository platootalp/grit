package github.grit.llm.util;

import java.util.List;
import java.util.Map;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentLoader;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.source.FileSystemSource;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.document.splitter.DocumentBySentenceSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmbeddingUtil {

	@Autowired
	private Map<String, EmbeddingModel> embeddingModelMap;

	public List<TextSegment> getFileSystemTextSegments(String url) {
		Document document = DocumentLoader.load(FileSystemSource.from(url), new TextDocumentParser());
		DocumentSplitter documentBySentenceSplitter = new DocumentBySentenceSplitter(128, 0, new OpenAiTokenizer(OpenAiChatModelName.GPT_4));
		DocumentSplitter splitter = new DocumentByParagraphSplitter(1024, 0, new OpenAiTokenizer(OpenAiChatModelName.GPT_4), documentBySentenceSplitter);
		return splitter.split(document);
	}

	public EmbeddingModel getEmbeddingModel(String modelName) {
		return embeddingModelMap.getOrDefault(modelName,
				new OpenAiEmbeddingModel(
						OpenAiEmbeddingModel.builder()
								.baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
								.apiKey("sk-f755ab0f995c43ffa206424bb2c43de2")
								.modelName("text-embedding-v2")));
	}

	public Document loadDocument(String url){
		return DocumentLoader.load(FileSystemSource.from(url), new TextDocumentParser());
	}
}
