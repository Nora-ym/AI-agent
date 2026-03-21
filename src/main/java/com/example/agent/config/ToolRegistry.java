package com.example.agent.config;

import com.example.agent.model.ToolDefinition;
import com.example.agent.tool.Tool;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
public class ToolRegistry {
    @Autowired
    private ApplicationContext applicationContext;  //Spring容器，用来获取所有的Bean
    //存放工具名->工具定义  Map<键值类型，值的类型> 变量名  = new 实现类型<>();
    private final Map<String, ToolDefinition> tools =new HashMap<>();


    /*
    启动时自动执行，扫描所有bean，找到带@tool注解的方法，注册到tools中
     */
    @PostConstruct
    public void init(){
        //1.获取所有Spring容器中所有Bean的名称 也就是所有被Spring管理的类
        String[] beanNames=applicationContext.getBeanDefinitionNames();

        //2.遍历每个Bean
        for (String beanName:beanNames){
            Object bean=applicationContext.getBean(beanName);//拿到对象本身
            Class<?> beanClass=bean.getClass();//拿到对象的类
            if (this.getClass().isInstance(bean)) {
                continue;
            }

            //3.
            for (Method method:beanClass.getDeclaredMethods()){//遍历这个对象的所有方法
                //4.检查这个类是否有注释
                if (method.isAnnotationPresent(Tool.class)){
                    //拿到注释
                    Tool annotation=method.getAnnotation(Tool.class);
                    //5.拿到注释的名字和描述
                    String toolName=annotation.name().isEmpty()?method.getName():annotation.name();
                    String description= annotation.description();

                    //6.解锁方法，哪怕是私有也能调用
                    method.setAccessible(true);
                    //7.tools上面定义的Map数组的变量名称，把拿到的东西都放进Map
                    //new ToolDefinition类里面的四个信息封装成一个档案一样的东西存在一起，然后就可以放进map数组里了

                    tools.put(toolName,new ToolDefinition(toolName, method,bean, description));
                    //输出一句信息
                    System.out.println("注册工具："+toolName+"-"+description);

                }

            }
        }
    }
    /*
    根据工具名称获取工具定义
     */
    public ToolDefinition getTool(String name){
        return tools.get(name);
    }
    /*
    获取所有工具的描述，用于构建给Ai的系统提示
     */
    public String getToolsDescription(){
        StringBuilder sb =new StringBuilder();
        sb.append("你可以调用提下工具：\n");
        for (ToolDefinition def : tools.values()){
            sb.append("- ").append(def.getName()).append(": ").append(def.getDescription()).append("\n");

        }

        return sb.toString();
    }


}
