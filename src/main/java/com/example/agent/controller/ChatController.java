package com.example.agent.controller;

import com.example.agent.engine.AgentEngine;
import com.example.agent.model.AgentStatus;
import com.example.agent.model.ChatRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/agent")
public class ChatController {
    @Autowired
    private AgentEngine agentEngine;
    /*

     */
    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest request){
        return agentEngine.process(request.getSessionId(), request.getMessage());
    }
    /*

     */
    @PostMapping(value = "/chat/stream", produces = org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AgentStatus> chatStream(@RequestBody ChatRequest request){
        return agentEngine.processStream(request.getSessionId(),request.getMessage());
    }


}
