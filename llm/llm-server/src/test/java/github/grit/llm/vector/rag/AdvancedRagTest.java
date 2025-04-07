package github.grit.llm.vector.rag;

import java.util.Collection;
import java.util.List;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Metadata;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import github.grit.llm.enums.ModelEnum;
import github.grit.llm.util.LangChain4jUtil;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AdvancedRagTest {

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
	@Autowired
	private LangChain4jUtil langChain4jUtil;

	@Test
	public void query() {
		ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(3);
		chatMemory.add(SystemMessage.from("Is a Java development engineer with nearly one year of work experience. "
				+ "They are interested in large model applications and LLMOps and want to improve their technical skills in this direction. "
				+ "They wish to deeply study the Spring framework to enhance their Java development skills and apply it in conjunction with LLMOps. "
				+ "They hope to quickly grow into a Java engineer capable of independently completing small projects, "
				+ "possessing end-to-end development capabilities and the ability to build projects from scratch. "
				+ "They can only study and develop projects in their spare time outside of work."));
		chatMemory.add(UserMessage.from("现在想要学习使用LangChain4j开发项目，请给我一些建议。"));
		chatMemory.add(AiMessage.from("SaaS工具开发结合大模型可以为你带来广阔的市场机会，尤其是利用你的Java开发经验，可以更高效地构建后端服务与数据处理系统。以下是详细的方向和建议：\n"
				+ "\n"
				+ "---\n"
				+ "\n"
				+ "## **1. SaaS 工具类型与应用场景**\n"
				+ "\n"
				+ "### **(1) 智能文档管理**\n"
				+ "- **功能**: 文档分类、智能检索、文本摘要、关键词提取，自动生成会议纪要。\n"
				+ "- **目标客户**: 企业办公、法务、咨询公司。\n"
				+ "- **技术建议**:\n"
				+ "    - NLP模型结合Spring Boot实现API服务。\n"
				+ "    - 文本存储使用Elasticsearch提高搜索性能。\n"
				+ "\n"
				+ "### **(2) 自动化代码审查与优化工具**\n"
				+ "- **功能**: 静态代码分析、性能优化建议、代码安全扫描。\n"
				+ "- **目标客户**: 中大型企业、开发团队。\n"
				+ "- **技术建议**:\n"
				+ "    - 后端基于Spring Boot + GraphQL提供代码分析服务。\n"
				+ "    - 集成GPT API 生成代码重构建议。\n"
				+ "    - 提供Jenkins插件以便持续集成环境中自动分析。\n"
				+ "\n"
				+ "### **(3) 客服与智能问答平台**\n"
				+ "- **功能**: 智能客服对话机器人，支持FAQ自动生成和复杂问题解答。\n"
				+ "- **目标客户**: 电商、金融、在线服务平台。\n"
				+ "- **技术建议**:\n"
				+ "    - 使用大模型（如OpenAI或本地部署的LLM）处理对话。\n"
				+ "    - 后端使用Spring Boot + WebSocket提供实时对话支持。\n"
				+ "\n"
				+ "### **(4) 数据分析与预测平台**\n"
				+ "- **功能**: 数据清洗、自动分析报告生成、预测模型辅助决策。\n"
				+ "- **目标客户**: 金融、零售、供应链企业。\n"
				+ "- **技术建议**:\n"
				+ "    - 后端使用Spring Boot集成大模型API进行数据分析。\n"
				+ "    - 数据可视化使用前端框架如React + Recharts。\n"
				+ "\n"
				+ "---\n"
				+ "\n"
				+ "## **2. SaaS 平台技术架构**\n"
				+ "\n"
				+ "### **(1) 技术选型**\n"
				+ "- **后端**: Spring Boot + Spring Cloud 微服务架构\n"
				+ "- **前端**: React 或 Vue.js\n"
				+ "- **数据库**: PostgreSQL / MySQL + Redis\n"
				+ "- **模型服务**: GPT API、Hugging Face 模型服务或本地模型（如LLama）\n"
				+ "- **消息队列**: RabbitMQ / Kafka\n"
				+ "- **容器与部署**: Docker + Kubernetes\n"
				+ "\n"
				+ "### **(2) 关键模块**\n"
				+ "- **用户管理**: 注册、登录、权限管理\n"
				+ "- **订阅与计费系统**: 支持按量收费或套餐订阅\n"
				+ "- **模型服务模块**: 封装大模型API，处理请求与响应\n"
				+ "- **日志与监控**: ELK堆栈进行监控分析\n"
				+ "- **高并发支持**: NGINX 负载均衡 + Redis 缓存优化\n"
				+ "\n"
				+ "---\n"
				+ "\n"
				+ "## **3. 商业变现模式**\n"
				+ "- **订阅服务**: 按月或按年收费，提供不同功能套餐。\n"
				+ "- **按量付费**: 根据API调用次数或处理数据规模收费。\n"
				+ "- **企业定制服务**: 提供白标服务或高级功能定制。\n"
				+ "- **增值服务**: 如高级模型分析、团队协作功能等。\n"
				+ "\n"
				+ "---\n"
				+ "\n"
				+ "## **4. 项目启动建议**\n"
				+ "- **第一步：需求分析与MVP设计**\n"
				+ "    - 确定工具的核心功能，优先开发MVP版本。\n"
				+ "- **第二步：模型选型与API封装**\n"
				+ "    - 选择稳定的大模型API，封装模型服务接口。\n"
				+ "- **第三步：技术架构设计与开发**\n"
				+ "    - 确定微服务架构，优先实现用户管理与模型调用模块。\n"
				+ "- **第四步：测试与迭代**\n"
				+ "    - 加强模型响应性能测试，确保高并发环境下的稳定性。\n"
				+ "- **第五步：上线与推广**\n"
				+ "    - 通过行业社群、博客、企业合作进行推广。\n"
				+ "\n"
				+ "---\n"
				+ "\n"
				+ "有兴趣针对某个具体的SaaS工具做更详细的讨论或技术设计吗？"));
		Metadata metadata = Metadata.from(UserMessage.from("我想要学习一下AI对话平台技术架构"), 1, chatMemory.messages());
		// 1.构造查询
		Query query = Query.from("我想要学习一下AI对话平台技术架构", metadata);
		// 2.查询转换  查询+记忆压缩
		QueryTransformer queryTransformer = CompressingQueryTransformer.builder()
				.chatLanguageModel(langChain4jUtil.getChatModel(ModelEnum.DEEPSEEK_V3.getName()))
				.build();
		Collection<Query> transform = queryTransformer.transform(query);

		// 内容检索器
		ContentRetriever contentRetriever = langChain4jUtil.getEmbeddingStoreContentRetriever();

		// 查询路由器
	}
}
