package github.grit.llm.rag;

import java.util.List;


import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.IngestionResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import github.grit.llm.enums.ModelEnum;
import github.grit.llm.util.LangChain4jUtil;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmbeddingTest {

	@Autowired
	private LangChain4jUtil langChain4jUtil;

	@Test
	public void dimension(){
		EmbeddingModel embeddingModel = langChain4jUtil.getEmbeddingModel("text-embedding-v3");
		System.out.println(embeddingModel.dimension());
	}


	@Test
	public void embeddingStore(){
		List<TextSegment> fileSystemTextSegments = langChain4jUtil.getFileSystemTextSegments("/Users/lijunyi/road/grit/note/AI/python.md");
		// 获取embedding模型
		EmbeddingModel embeddingModel = langChain4jUtil.getEmbeddingModel(ModelEnum.TEXT_EMBEDDING_V3.getName());
//		fileSystemTextSegments.forEach(item->{
//			Response<Embedding> response = embeddingModel.embed(item);
//			System.out.println(response);
//		});
		Response<List<Embedding>> listResponse = embeddingModel.embedAll(fileSystemTextSegments);
//		System.out.println(listResponse);

		// 向量库
		EmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();
		List<String> ids = store.addAll(listResponse.content());
		System.out.println(ids);
	}

	@Test
	public void embeddingStoreIngestor(){
		EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
				.embeddingModel(langChain4jUtil.getEmbeddingModel(ModelEnum.TEXT_EMBEDDING_V3.getName()))
				.embeddingStore(new InMemoryEmbeddingStore<>())
				.build();
		Document document = langChain4jUtil.loadDocument("/Users/lijunyi/road/grit/note/direction/test.txt");
		IngestionResult ingest = ingestor.ingest(document);
		System.out.println(ingest.tokenUsage());
	}

}
