package com.example.agent.util;

import com.example.agent.model.ChatMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LlmClient {

    public String chat(List<ChatMessage> history, String toolsDescription) {
        // 如果最后一条消息是工具返回的结果，直接生成最终回答，避免死循环
        if (!history.isEmpty()) {
            ChatMessage last = history.get(history.size() - 1);
            if (last.getRole() == ChatMessage.Role.TOOL) {
                return "最终回答：根据工具结果，" + last.getContent();
            }
        }

        // 获取最后一条用户消息
        ChatMessage lastUserMsg = null;
        for (int i = history.size() - 1; i >= 0; i--) {
            if (history.get(i).getRole() == ChatMessage.Role.USER) {
                lastUserMsg = history.get(i);
                break;
            }
        }

        if (lastUserMsg == null || lastUserMsg.getContent() == null || lastUserMsg.getContent().trim().isEmpty()) {
            return "最终回答：我没收到有效的消息。";
        }

        String userInput = lastUserMsg.getContent();

        // 简单规则判断
        if (userInput.contains("天气")) {
            return "TOOL_CALL: get_weather(city=北京)";
        } else if (userInput.contains("计算")) {
            String expr = userInput.replace("计算", "").trim();
            return "TOOL_CALL: calculate(expression=" + expr + ")";
        } else {
            return "最终回答：你说的是“" + userInput + "”，我不确定你需要什么帮助。";
        }
    }
}