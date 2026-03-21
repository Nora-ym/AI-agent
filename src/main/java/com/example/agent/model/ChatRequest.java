package com.example.agent.model;

public class ChatRequest {
    public String getSessionId() {
        return sessionId;
    }
    public void setSessionId(String sessionId) {
        this.sessionId = this.sessionId;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    private String message;//用户输入的消息
    private String sessionId;//会话Id，用来区分不同用户
}
