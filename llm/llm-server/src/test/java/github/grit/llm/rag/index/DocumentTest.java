package github.grit.llm.rag.index;

import java.util.List;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentLoader;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.DocumentTransformer;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.source.ClassPathSource;
import dev.langchain4j.data.document.source.FileSystemSource;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.document.splitter.DocumentBySentenceSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DocumentTest {

	@Test
	public void loadDocument(){
		System.out.println(System.getProperty("user.dir"));
		Document document1 = DocumentLoader.load(ClassPathSource.from("test.txt"), new TextDocumentParser());
		System.out.println(document1.metadata());
		System.out.println(document1.text());

		Document document2 = DocumentLoader.load(FileSystemSource.from("/Users/lijunyi/road/grit/note/AI/AI学习路径.md"), new TextDocumentParser());
		System.out.println(document2.toTextSegment());
		System.out.println(document2.text());
		System.out.println(document2.metadata());
	}

	@Test
	public void documentTransformer(){
		Document document = DocumentLoader.load(ClassPathSource.from("test.txt"), new TextDocumentParser());
		DocumentTransformer transformer = TroubleTransformer.builder().build();
		Document transformedDocument = transformer.transform(document);
		System.out.println(transformedDocument.text());
		System.out.println(transformedDocument.metadata());
	}

	@Test
	public void documentSplitter(){
		Document document = DocumentLoader.load(FileSystemSource.from("/Users/lijunyi/road/grit/note/AI/AI学习路径.md"), new TextDocumentParser());
//		System.out.println(document.toTextSegment());
		DocumentSplitter documentBySentenceSplitter = new DocumentBySentenceSplitter(128, 0, new OpenAiTokenizer(OpenAiChatModelName.GPT_4));
		DocumentSplitter splitter = new DocumentByParagraphSplitter(1024,0, new OpenAiTokenizer(OpenAiChatModelName.GPT_4), documentBySentenceSplitter);
		List<TextSegment> textSegmentList = splitter.split(document);
		for (TextSegment textSegment : textSegmentList) {
			System.out.println(textSegment.text() + textSegment.metadata() +"\n\n\n");
		}
	}
}
