package com.example.agent.model;

import java.lang.reflect.Method;

public class ToolDefinition {
    public ToolDefinition(String name, Method method, Object bean, String description) {
        this.name = name;
        this.method = method;
        this.bean = bean;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    private String name;
    private String description;
    private Object bean;
    private Method method;

}
