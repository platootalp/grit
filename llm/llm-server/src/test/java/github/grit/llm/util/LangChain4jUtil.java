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
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import dev.langchain4j.rag.query.router.DefaultQueryRouter;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.web.search.WebSearchEngine;
import dev.langchain4j.web.search.google.customsearch.GoogleCustomWebSearchEngine;
import github.grit.llm.service.Copilot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LangChain4jUtil {

	@Autowired
	private Map<String, EmbeddingModel> embeddingModelMap;
	@Autowired
	private Map<String, ChatLanguageModel> modelBeans;
	@Autowired
	private Map<String, StreamingChatLanguageModel> streamingModelBeans;

	public List<TextSegment> getFileSystemTextSegments(String url) {
		Document document = DocumentLoader.load(FileSystemSource.from(url), new TextDocumentParser());
		DocumentSplitter documentBySentenceSplitter = new DocumentBySentenceSplitter(128, 0, new OpenAiTokenizer(OpenAiChatModelName.GPT_4));
		DocumentSplitter splitter = new DocumentByParagraphSplitter(1024, 0, new OpenAiTokenizer(OpenAiChatModelName.GPT_4), documentBySentenceSplitter);
		return splitter.split(document);
	}

	public EmbeddingModel getEmbeddingModel() {
		return embeddingModelMap.getOrDefault(null,
				new OpenAiEmbeddingModel(
						OpenAiEmbeddingModel.builder()
								.baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
								.apiKey("sk-f755ab0f995c43ffa206424bb2c43de2")
								.modelName("text-embedding-v2")));
	}

	public EmbeddingModel getEmbeddingModel(String modelName) {
		return embeddingModelMap.getOrDefault(modelName,
				new OpenAiEmbeddingModel(
						OpenAiEmbeddingModel.builder()
								.baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
								.apiKey("sk-f755ab0f995c43ffa206424bb2c43de2")
								.modelName("text-embedding-v2")));
	}

	public Document loadDocument(String url) {
		return DocumentLoader.load(FileSystemSource.from(url), new TextDocumentParser());
	}

	public ChatLanguageModel getChatModel(String modelName) {
		return modelBeans.getOrDefault(modelName,
				new OpenAiChatModel(
						OpenAiChatModel.builder()
								.baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
								.apiKey("sk-f755ab0f995c43ffa206424bb2c43de2")
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
				.embeddingModel(getEmbeddingModel())
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
}
