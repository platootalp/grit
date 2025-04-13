package github.grit.llm.rag.index;

import java.util.List;
import java.util.Map;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import github.grit.llm.enums.ModelEnum;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MilvusTest {

	@Autowired
	private Map<String, EmbeddingModel> embeddingModelMap;

//	private static MilvusClientV2 milvusClient;
//
//	@BeforeAll
//	static void setup() {
//		ConnectConfig connectConfig = ConnectConfig.builder()
//				.uri("http://localhost:19530")
//				.token("root:Milvus")
//				.build();
//
//		milvusClient = new MilvusClientV2(connectConfig);
//	}
//
//	@AfterAll
//	static void teardown() {
//		if (milvusClient != null) {
//			milvusClient.close();
//		}
//	}

	@Test
	public void dimension(){
		EmbeddingModel embeddingModel = embeddingModelMap.get(ModelEnum.TEXT_EMBEDDING_V3.getName());
		System.out.println(embeddingModel.dimension());
	}

	@Test
	public void deleteCollection(){
		MilvusEmbeddingStore store = MilvusEmbeddingStore.builder()
				.uri("http://localhost:19530")
				.token("root:Milvus")
				.collectionName("test_collection")
				.dimension(1024)
				.build();
		store.dropCollection("test_collection");
	}

	@Test
	public void query() {
		EmbeddingStore<TextSegment> embeddingStore = MilvusEmbeddingStore.builder()
				.uri("http://localhost:19530")
				.token("root:Milvus")
				.collectionName("collection")
				.dimension(1024)
				.build();

		EmbeddingModel embeddingModel = embeddingModelMap.get(ModelEnum.TEXT_EMBEDDING_V3.getName());
		Embedding queryEmbedding = embeddingModel.embed("What is your favourite sport?").content();
		EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
				.queryEmbedding(queryEmbedding)
				.maxResults(1)
				.minScore(0.8)
				.build();
		EmbeddingSearchResult<TextSegment> result = embeddingStore.search(request);
		List<EmbeddingMatch<TextSegment>> matches = result.matches();
		for (EmbeddingMatch<TextSegment> match : matches) {
			System.out.println(match.score());
			System.out.println(match.embedded().text());
		}
	}
	@Test
	public void example() {
		EmbeddingStore<TextSegment> embeddingStore = MilvusEmbeddingStore.builder()
				.uri("http://localhost:19530")
				.token("root:Milvus")
				.collectionName("collection")
				.dimension(1024)
				.build();

		EmbeddingModel embeddingModel = embeddingModelMap.get(ModelEnum.TEXT_EMBEDDING_V3.getName());

		TextSegment segment1 = TextSegment.from("I like football.");
		Embedding embedding1 = embeddingModel.embed(segment1).content();
		embeddingStore.add(embedding1, segment1);

		TextSegment segment2 = TextSegment.from("The weather is good today.");
		Embedding embedding2 = embeddingModel.embed(segment2).content();
		embeddingStore.add(embedding2, segment2);

		Embedding queryEmbedding = embeddingModel.embed("What is your favourite sport?").content();
//		List<EmbeddingMatch<TextSegment>> relevant = embeddingStore.findRelevant(queryEmbedding, 1);
//		EmbeddingMatch<TextSegment> embeddingMatch = relevant.get(0);
//
//		System.out.println(embeddingMatch.score()); // 0.8144287765026093
//		System.out.println(embeddingMatch.embedded().text()); // I like football.
	}
}
