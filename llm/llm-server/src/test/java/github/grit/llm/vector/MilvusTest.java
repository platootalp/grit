package github.grit.llm.vector;

import java.util.List;
import java.util.Map;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import github.grit.llm.enums.ModelEnum;
import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class MilvusTest {

	@Autowired
	private Map<String, EmbeddingModel> embeddingModelMap;

	private static MilvusClientV2 milvusClient;

	@BeforeAll
	static void setup() {
		ConnectConfig connectConfig = ConnectConfig.builder()
				.uri("https://in03-c9ca0cbc11f0bd5.serverless.ali-cn-hangzhou.cloud.zilliz.com.cn:19530")
				.token("Bearer a5ad07067b0ea51e6d498d6785ac247139fb25723dbb98b81e7a311761e65aba49ac6934b49b4a7c1661288269e7c1c9237929f6")
				.build();

		milvusClient = new MilvusClientV2(connectConfig);
	}

	@AfterAll
	static void teardown() {
		if (milvusClient != null) {
			milvusClient.close();
		}
	}

	@Test
	public void test() {
		EmbeddingStore<TextSegment> embeddingStore = MilvusEmbeddingStore.builder()
				.uri("https://in03-7934be782016d47.serverless.gcp-us-west1.cloud.zilliz.com:19530")
				.token("Bearer 0c7dbd1ad54f5215c314a60810130ef5d6b0c1cfdbbd8fa224ea15da6102714a3b174ee8e0fa4abe09db26b83bf4cc7e30e69325")
				.collectionName("test_collection")
				.dimension(384)
				.build();

		EmbeddingModel embeddingModel = embeddingModelMap.get(ModelEnum.TEXT_EMBEDDING_V3.getName());

		TextSegment segment1 = TextSegment.from("I like football.");
		Embedding embedding1 = embeddingModel.embed(segment1).content();
		embeddingStore.add(embedding1, segment1);

		TextSegment segment2 = TextSegment.from("The weather is good today.");
		Embedding embedding2 = embeddingModel.embed(segment2).content();
		embeddingStore.add(embedding2, segment2);

		Embedding queryEmbedding = embeddingModel.embed("What is your favourite sport?").content();
		List<EmbeddingMatch<TextSegment>> relevant = embeddingStore.findRelevant(queryEmbedding, 1);
		EmbeddingMatch<TextSegment> embeddingMatch = relevant.get(0);

		System.out.println(embeddingMatch.score()); // 0.8144287765026093
		System.out.println(embeddingMatch.embedded().text()); // I like football.
	}
}
