# AI-agent
An simple agent example

### 一、项目思路：
#### 1. 什么是AI Agent智能体？
AI Agent是一个能够自主理解用户意图、调用外部工具、逐步推理并返回结果的程序。与普通的大模型问答不同，Agent可以执行具体操作，比如查询天气、计算数学题、查询数据库等。

#### 2. 工具调用的核心流程
<img width="1607" height="3151" alt="deepseek_mermaid_20260319_4d1923" src="https://github.com/user-attachments/assets/6a87267c-0c27-4866-9c7a-6fc5e36e0bef" />

#### 3.关键技术点
-工具定义：使用注解声明工具方法，自动注册到工具库。

-LLM交互：构造包含系统提示、工具描述、对话历史的Prompt，调用大模型API。

-输出解析：解析LLM返回的文本，判断是工具调用还是最终回答。

-工具执行：通过反射执行工具方法，处理参数和返回值。

-状态管理：维护对话上下文（包括用户消息、助手消息、工具结果）。

-流式响应：使用SSE将执行步骤实时推送给前端。

#### 4.本项目实现的功能
✅ 一个Spring Boot REST服务，提供/chat和/chat/stream接口。
✅ 支持定义多个工具（如get_weather、calculate）。
✅ 自动注册工具，无需手动添加。
✅ 支持多轮对话和工具调用。
✅ 流式输出执行过程（Thinking、Executing、Result）。


### 二、项目结构
```bash
src/main/java/com/example/agent/

├── AgentApplication.java
├── config/
│   ├── LlmConfig.java                 # LLM配置（模拟或真实API）
│   └── ToolRegistry.java               # 工具注册中心
├── controller/
│   └── ChatController.java             # 聊天接口
├── engine/
│   ├── AgentEngine.java                # Agent核心引擎
│   └── ToolExecutor.java                # 工具执行器
├── model/
│   ├── ChatMessage.java                 # 对话消息
│   ├── ChatRequest.java                  # 请求体
│   ├── AgentStatus.java                   # SSE状态封装
│   └── ToolDefinition.java                # 工具元数据
├── tool/
│   ├── Tool.java                         # 工具注解
│   ├── WeatherTool.java                   # 示例工具1
│   └── CalculatorTool.java                 # 示例工具2
└── util/
    └── LlmClient.java                      # 模拟LLM客户端

```
### 三、详细代码实现
#### 1. Maven依赖（pom.xml）
#### 2. 启动类
#### 3. 工具注解@Tool
#### 4. 工具元数据模型
#### 5. 工具注册中心
#### 6. 对话消息模型
#### 7. 请求和响应模型
#### 8. 模拟LLM客户端
#### 9. 工具执行器
#### 10. agent引擎
#### 11. 示例工具实现
#### 12. 控制器（提供API接口）


### 四、如何使用
#### 1. 启动Spring Boot应用：运行AgentApplication.main()。
#### 2. 测试非流式接口：
```bash
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId":"123","message":"北京天气怎么样？"}'
```
#### 3. 测试流式接口：
```bash
curl -N -X POST http://localhost:8080/api/agent/chat/stream \
  -H "Content-Type: application/json" \
  -d '{"sessionId":"123","message":"计算 12+5"}'
```
### 五、代码说明
-工具注册：通过ToolRegistry扫描所有带有@Tool注解的方法，自动注册，无需手动维护。

-Agent循环：在AgentEngine中实现，每次调用LLM后判断是否是工具调用，执行工具并将结果加入历史，直到得到最终回答。

-模拟LLM：LlmClient演示了如何构造包含工具描述的提示，实际使用时可替换为调用真实大模型API（如OpenAI、DeepSeek）。

-流式输出：使用Project Reactor的Flux和Sinks，将每个阶段状态推送给客户端。

### 六、扩展建议
1.替换为真实LLM：使用OpenAI SDK，将LlmClient改造为调用API，并正确处理工具调用格式。

2.参数绑定优化：目前工具执行时参数绑定是简化的，实际应通过ParameterNameDiscoverer获取方法参数名，或使用类似Spring MVC的@RequestParam注解。

3.添加更多工具：如查询数据库、发送邮件等。

4.持久化会话：将对话历史存储到Redis或数据库。

5.添加认证和限流：保障服务安全


























