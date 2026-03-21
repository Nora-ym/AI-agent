package com.example.agent.model;
/*
   表示一条对话消息，可以是用户，AI说的，或者工具返回的结果
    */
public class ChatMessage {
   public enum Role{
       USER,//用户
       ASSISTANT,//AI助手
       TOOL//工具执行结果
   }


   //第一个构造方法，只包含普通消息，这样后续调用的时候方便使用
    //不然三个的一起没有使用工具的时候还得传一个空参
    public ChatMessage(Role role, String content) {
        this.role = role;
        this.content = content;
    }


    /*
    this(role, content); 调用上面的构造参数，专门给工具返回的消息使用
     */
    public ChatMessage(String toolName, String content, Role role) {
        this(role, content);
        this.toolName = toolName;

    }

    private Role role;
   private String content;
   private String toolName;

    public Role getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    public String getToolName() {
        return toolName;
    }
}
