package github.grit.llm.rag;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.aggregator.ContentAggregator;
import dev.langchain4j.rag.content.aggregator.DefaultContentAggregator;
import dev.langchain4j.rag.content.injector.ContentInjector;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Metadata;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.rag.query.router.LanguageModelQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import github.grit.llm.enums.ModelEnum;
import github.grit.llm.util.LangChain4jUtil;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AdvancedRagTest {

	@Autowired
	private LangChain4jUtil langChain4jUtil;

	//	public void contentRetriever(){
	//		ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
	//				.embeddingStore(embeddingStore)
	//				.embeddingModel(embeddingModel)
	//				.maxResults(3)   // 返回最多3条结果
	//				.dynamicMaxResults(query -> 3)  // 根据查询动态指定maxResults
	//				.minScore(0.75)  // 过滤分数低于0.75的内容
	//				.dynamicMinScore(query -> 0.75)  // 根据查询动态指定minScore
	//				.filter(metadataKey("userId").isEqualTo("12345"))  // 过滤指定用户的内容
	//				.dynamicFilter(query -> {
	//					String userId = getUserId(query.metadata().chatMemoryId());
	//					return metadataKey("userId").isEqualTo(userId);  // 动态过滤
	//				})
	//				.build();
	//	}

	@Test
	public void query() throws InterruptedException {
		ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(3);
		chatMemory.add(SystemMessage.from("Is a Java development engineer with nearly one year of work experience. "
				+ "They are interested in large model applications and LLMOps and want to improve their technical skills in this direction. "
				+ "They wish to deeply study the Spring framework to enhance their Java development skills and apply it in conjunction with LLMOps. "
				+ "They hope to quickly grow into a Java engineer capable of independently completing small projects, "
				+ "possessing end-to-end development capabilities and the ability to build projects from scratch. "
				+ "They can only study and develop projects in their spare time outside of work."));
		chatMemory.add(UserMessage.from("我现在想要学习使用LangChain4j开发项目，请给我一些建议。"));
		ChatLanguageModel chatModel = langChain4jUtil.getChatModel(ModelEnum.DEEPSEEK_V3.getName());
		Metadata metadata = Metadata.from(UserMessage.from("我想要学习一下AI对话平台技术架构"), 1, chatMemory.messages());
		// 1.构造查询
		Query query = Query.from("我想要学习一下AI对话平台技术架构", metadata);
		// 2.查询转换  查询+记忆压缩
		QueryTransformer queryTransformer = CompressingQueryTransformer.builder()
				.chatLanguageModel(chatModel)
				.build();
		Collection<Query> transformQueries= queryTransformer.transform(query);

		// 3.查询路由
		// 内容检索器
		ContentRetriever embeddingStoreContentRetriever = langChain4jUtil.getEmbeddingStoreContentRetriever();
		ContentRetriever webSearchContentRetriever = langChain4jUtil.getWebSearchContentRetriever();
		// 查询路由器
		Map<ContentRetriever, String> retrieverToDescription = Map.of(
				embeddingStoreContentRetriever, "向量数据库内容检索器，在向量库中根据余弦相似度检索相关内容",
				webSearchContentRetriever, "网页搜索内容检索器，使用Google搜索引擎进行网页搜索");
		QueryRouter queryRouter = LanguageModelQueryRouter.builder()
				.chatLanguageModel(chatModel)
				.retrieverToDescription(retrieverToDescription)
				.build();
		// 4. 查询检索
		Map<Query, Collection<List<Content>>> queryToContents = transformQueries.stream()
				.collect(Collectors.toMap(
						q -> q,
						q -> queryRouter.route(q).stream()
								.map(retriever -> retriever.retrieve(q))
								.collect(Collectors.toList())
				));

		// 5.内容聚合
		ContentAggregator contentAggregator = new DefaultContentAggregator();
		List<Content> aggregate = contentAggregator.aggregate(queryToContents);

		// 6.内容注入
		ContentInjector contentInjector = DefaultContentInjector.builder().build();
		UserMessage message = contentInjector.inject(aggregate, UserMessage.from("我想要学习一下AI对话平台技术架构"));

		// 7.模型响应
		StreamingChatLanguageModel streamingChatModel = langChain4jUtil.getStreamingChatModel(ModelEnum.DEEPSEEK_V3.getName());
		CountDownLatch latch = new CountDownLatch(1);
		streamingChatModel.chat(Collections.singletonList(message), new StreamingChatResponseHandler() {
			@Override
			public void onPartialResponse(String partialResponse) {
				System.out.println(partialResponse + "\n");
			}

			@Override
			public void onCompleteResponse(ChatResponse completeResponse) {
				System.out.println(completeResponse);
				latch.countDown(); // 完成后释放锁
			}

			@Override
			public void onError(Throwable error) {
				error.printStackTrace();
				latch.countDown(); // 完成后释放锁
			}
		});
		latch.await(); // 主线程等待，防止测试提前退出
	}

	//	@Bean
	//	public Copilot assistant(ChatLanguageModel chatLanguageModel, EmbeddingStore<TextSegment> embeddingStore) {
	//
	//		DefaultRetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
	//				.queryTransformer()  // 查询增强
	//				.contentRetriever() // 内容源 单个直接配置
	//				.queryRouter(new DefaultQueryRouter())// 多个内容源，路由
	//				.contentAggregator() // 匹配结果聚合
	//				.contentInjector()   // 结果提示词注入
	//				.executor()  // 并行化
	//				.build();
	//
	//		return AiServices.builder(Copilot.class)
	//				.chatLanguageModel(chatLanguageModel)
	//				.chatMemory(MessageWindowChatMemory.withMaxMessages(10))
	//				.retrievalAugmentor(retrievalAugmentor)
	//				.build();
	//	}
}
