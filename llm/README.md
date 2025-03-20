# LLM 对话系统

基于 Spring Boot 和 LangChain4j 实现的 LLM 对话系统后端服务。

## 技术栈

- Spring Boot 3.2.x
- LangChain4j
- WebSocket
- MySQL
- MyBatis-Plus
- Redis

## 系统架构

### 核心模块

1. 对话管理模块
   - 会话管理
   - 消息历史记录
   - 流式响应支持

2. LLM 集成模块
   - LangChain4j 集成
   - 模型管理
   - 参数配置

3. 用户设置模块
   - 个性化配置
   - API 密钥管理

### 数据模型

1. Conversation（会话）
   - id: String (UUID)
   - title: String
   - created_at: Timestamp
   - updated_at: Timestamp

2. Message（消息）
   - id: String (UUID)
   - conversation_id: String (外键)
   - content: String
   - role: Enum (USER/ASSISTANT)
   - created_at: Timestamp

3. UserSettings（用户设置）
   - id: String (UUID)
   - default_model: String
   - temperature: Float
   - api_key: String (加密存储)
   - theme: String
   - language: String

## API 接口

详细的 API 接口文档请参考 [API.md](llm-ui/API.md)

## 开发环境设置

1. 安装依赖
   ```bash
   ./mvnw install
   ```

2. 配置环境变量
   创建 `application.yml` 文件：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/llm_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
       username: your_username
       password: your_password
       driver-class-name: com.mysql.cj.jdbc.Driver
     redis:
       host: localhost
       port: 6379

   mybatis-plus:
     mapper-locations: classpath*:/mapper/**/*.xml
     type-aliases-package: github.grit.llm.entity
     configuration:
       map-underscore-to-camel-case: true
     global-config:
       db-config:
         id-type: assign_uuid
         logic-delete-field: deleted
         logic-delete-value: 1
         logic-not-delete-value: 0

   langchain4j:
     model: gpt-3.5-turbo
     temperature: 0.7
     api-key: your_api_key
   ```

3. 运行应用
   ```bash
   ./mvnw spring-boot:run
   ```

## 部署

1. 构建 JAR 包
   ```bash
   ./mvnw clean package
   ```

2. 运行应用
   ```bash
   java -jar target/llm-0.0.1-SNAPSHOT.jar
   ```

## 注意事项

1. API 密钥安全
   - 生产环境中使用环境变量或密钥管理系统
   - 避免在代码中硬编码密钥

2. 数据安全
   - 敏感数据加密存储
   - 定期数据备份

3. 性能优化
   - 使用 Redis 缓存会话数据
   - 异步处理长时间运行的任务

## 贡献指南

1. Fork 项目
2. 创建特性分支
3. 提交变更
4. 发起 Pull Request

## 许可证

MIT License 