package com.example.agent.engine;

import com.example.agent.model.AgentStatus;
import com.example.agent.model.ChatMessage;
import com.example.agent.util.LlmClient;
import com.example.agent.config.ToolRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/*
核心：管理对话历史，控制循环，调用LLM和工具
 */
@Component
public class AgentEngine {
    @Autowired
    private LlmClient llmClient;
    @Autowired
    private ToolExecutor toolExecutor;
    @Autowired
    private ToolRegistry toolRegistry;

    //记录每个人的聊天记录
    private final ConcurrentHashMap<String , List<ChatMessage>> sessions=new ConcurrentHashMap<>();

    //存实时播报的喇叭（流式聊天用）
    private final ConcurrentHashMap<String, Sinks.Many<AgentStatus>> sinks;

    {
        sinks = new ConcurrentHashMap<>();
    }

    /*

     */
    public String process(String sessionId, String userMessage) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            sessionId = "default";
        }

        List<ChatMessage> history = sessions.computeIfAbsent(sessionId, k -> new ArrayList<>());
        history.add(new ChatMessage(ChatMessage.Role.USER, userMessage));

        int maxSteps = 5;
        int step = 0;

        while (step < maxSteps) {
            String llmResponse = llmClient.chat(history, toolRegistry.getToolsDescription());
            history.add(new ChatMessage(ChatMessage.Role.ASSISTANT, llmResponse));

            if (toolExecutor.isToolCall(llmResponse)) {
                try {
                    String toolResult = toolExecutor.execute(llmResponse);
                    history.add(new ChatMessage(extractToolName(llmResponse), toolResult,ChatMessage.Role.TOOL ));
                } catch (Exception e) {
                    return "工具执行出错：" + e.getMessage();
                }
            } else {
                return llmResponse;
            }
            step++;
        }
        return "已达到最大步骤限制，可能未完成。";
    }
/*
流式处理：每一步都实时推送状态给前端
 */
public Flux<AgentStatus> processStream(String sessionId, String userMessage) {
    // 1. 空值保护：必须放在最前面，且在所有使用 sessionId 的语句之前
    if (sessionId == null || sessionId.trim().isEmpty()) {
        sessionId = "default";
    }

    // 2. 创建 Sink
    final Sinks.Many<AgentStatus> sink = Sinks.many().multicast().onBackpressureBuffer();

    // 3. 现在 sessionId 一定不为 null，可以安全放入 map
    sinks.put(sessionId, sink);

    // 4. 异步执行 Agent 逻辑
    String finalSessionId = sessionId;
    new Thread(() -> {
        try {
            List<ChatMessage> history = sessions.computeIfAbsent(finalSessionId, k -> new ArrayList<>());
            history.add(new ChatMessage(ChatMessage.Role.USER, userMessage));

            int maxSteps = 5;
            int step = 0;
            while (step < maxSteps) {
                sink.tryEmitNext(new AgentStatus(AgentStatus.Phase.THINKING, "正在思考...", null, false));
                String llmResponse = llmClient.chat(history, toolRegistry.getToolsDescription());
                history.add(new ChatMessage(ChatMessage.Role.ASSISTANT, llmResponse));

                if (toolExecutor.isToolCall(llmResponse)) {
                    String toolName = extractToolName(llmResponse);
                    sink.tryEmitNext(new AgentStatus(AgentStatus.Phase.EXECUTING, "正在调用工具：" + toolName, toolName, false));
                    try {
                        String toolResult = toolExecutor.execute(llmResponse);
                        history.add(new ChatMessage(toolName,toolResult,ChatMessage.Role.TOOL  ));
                        sink.tryEmitNext(new AgentStatus(AgentStatus.Phase.EXECUTING, "工具返回：" + toolResult, toolName, false));
                    } catch (Exception e) {
                        sink.tryEmitNext(new AgentStatus(AgentStatus.Phase.ERROR, "工具执行错误：" + e.getMessage(), null, true));
                        break;
                    }
                } else {
                    sink.tryEmitNext(new AgentStatus(AgentStatus.Phase.RESULT, llmResponse, null, true));
                    break;
                }
                step++;
            }
            if (step >= maxSteps) {
                sink.tryEmitNext(new AgentStatus(AgentStatus.Phase.RESULT, "已达到最大步骤限制，可能未完成。", null, true));
            }
            sink.tryEmitComplete();
        } catch (Exception e) {
            e.printStackTrace();
            sink.tryEmitNext(new AgentStatus(AgentStatus.Phase.ERROR, "系统错误：" + e.getMessage(), null, true));
            sink.tryEmitComplete();
        } finally {
            sinks.remove(finalSessionId);
        }
    }).start();

    return sink.asFlux();
}
    /*

     */
    private String extractToolName(String toolCallText){
        java.util.regex.Pattern p=java.util.regex.Pattern.compile("TOOL_CALL:\\s*(\\w+)\\(");
        java.util.regex.Matcher m=p.matcher(toolCallText);
        if (m.find()){
            return m.group(1);
        }
        return "未知工具";
    }
}
