package github.grit.llm.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentLoader;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.source.FileSystemSource;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.document.splitter.DocumentBySentenceSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.jina.JinaScoringModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.model.scoring.ScoringModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.web.search.WebSearchEngine;
import dev.langchain4j.web.search.google.customsearch.GoogleCustomWebSearchEngine;
import github.grit.llm.enums.ModelEnum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static dev.langchain4j.model.chat.Capability.RESPONSE_FORMAT_JSON_SCHEMA;

@Component
public class LangChain4jUtil {

	@Autowired
	private Map<String, EmbeddingModel> embeddingModelMap;
	@Autowired
	private Map<String, ChatLanguageModel> modelBeans;
	@Autowired
	private Map<String, StreamingChatLanguageModel> streamingModelBeans;

	public Document loadDocument(String url) {
		return DocumentLoader.load(FileSystemSource.from(url), new TextDocumentParser());
	}

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
								.modelName(ModelEnum.TEXT_EMBEDDING_V2.getName())));
	}

	public ChatLanguageModel getChatModel(String modelName) {
		return modelBeans.getOrDefault(modelName,
				new OpenAiChatModel(
						OpenAiChatModel.builder()
								.baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
								.apiKey("sk-f755ab0f995c43ffa206424bb2c43de2")
								.supportedCapabilities(Set.of(RESPONSE_FORMAT_JSON_SCHEMA))
								.strictJsonSchema(true)
								.logRequests(true)
								.logResponses(true)
								.modelName("qwen-turbo")));
	}

	public StreamingChatLanguageModel getStreamingChatModel(String modelName) {
		return streamingModelBeans.getOrDefault(modelName,
				new OpenAiStreamingChatModel(
						OpenAiStreamingChatModel.builder()
								.baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
								.apiKey("sk-f755ab0f995c43ffa206424bb2c43de2")
								.modelName("qwen-turbo")));
	}

	public <T> EmbeddingStore<T> getEmbeddingStore() {
		return new InMemoryEmbeddingStore<>();
	}

	public ContentRetriever getEmbeddingStoreContentRetriever() {
		return EmbeddingStoreContentRetriever.builder()
				.embeddingStore(getEmbeddingStore())
				.embeddingModel(getEmbeddingModel(null))
				.maxResults(5)
				.minScore(0.75)
				.build();

	}

	public ContentRetriever getWebSearchContentRetriever() {
		WebSearchEngine googleSearchEngine = GoogleCustomWebSearchEngine.builder()
				.apiKey("AIzaSyCf3XWg2mp0t8ltohdtb2jyq4e-fLGcL9U")
				.csi("b64d613e0672e410f")
				.build();
		return WebSearchContentRetriever.builder()
				.webSearchEngine(googleSearchEngine)
				.maxResults(5)
				.build();
	}

	public ScoringModel getScoringModel() {
		return JinaScoringModel.builder()
				.apiKey("jina_ab1f8436c1184fa580305c6d673a5fa23t1ZTexYhb_VQo3AuOX94L2qPv05")
				.modelName(ModelEnum.JINA_RERANKER_V2_BASE_MULTILINGUAL.getName())
				.build();
	}
}
