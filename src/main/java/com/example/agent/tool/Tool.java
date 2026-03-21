package com.example.agent.tool;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Tool {
    /*
    工具的名称，如果为空则使用方法名
     */
    String name() default "";
    //工具的描述用于告诉LLm这个工具的作用
    String description() default "";
    boolean enabled() default true;
}
