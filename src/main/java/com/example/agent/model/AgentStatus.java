package com.example.agent.model;
/*
用于流式输出，用于告诉你目前agent在干什么
 */
public class AgentStatus {
    public enum Phase{
        THINKING,//正在思考下一步
        EXECUTING,//正在执行工具
        RESULT,//返回最终结果
        ERROR//出错
    }

    public AgentStatus(Phase phase, String content, String toolName, boolean done) {
        this.phase = phase;
        this.content = content;
        this.toolName = toolName;
        this.done = done;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    private Phase phase;
    private String content;//详细描述
    private String toolName;//如果是执行工具，工具名称
    private boolean done;//是否完成
}
