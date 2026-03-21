package com.example.agent.engine;


import com.example.agent.config.ToolRegistry;
import com.example.agent.model.ToolDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ToolExecutor {
    @Autowired
    private ToolRegistry toolRegistry;//工具收纳

    //私有静态不能修改的常量Pattern  变量名            compile()方法是固定的将字符串正则编译成Java能识别的匹配模板
    private static final Pattern TOOL_CALL_PATTERN=Pattern.compile("TOOL_CALL:\\s*(\\w+)\\(([^)]*)\\)");

    /*
        判断一段文本是不是工具调用指令
         */
    public boolean isToolCall(String text){
        //检查是否为空并且是否以那个开头，都满足才返回true
        return text!=null&&text.trim().startsWith("TOOL_CALL:");
    }
    /*
    执行工具调用
    toolCallText：例如"TOOL_CALL: get_weather(city=北京)"
    工具执行后的结果字符串
     */
    //接收完整暗号，
    public String execute(String toolCallText)throws Exception{
        //解析工具名和参数部分
        Matcher matcher=TOOL_CALL_PATTERN.matcher(toolCallText.trim());
        if (!matcher.matches()){
            throw new IllegalArgumentException("工具调用格式错误"+toolCallText);
        }

        String toolName= matcher.group(1);
        String paramsPart= matcher.group(2);

        //根据工具名，从Map里取出工具
        ToolDefinition def=toolRegistry.getTool(toolName);
        //判断工具是否存在
        if (def==null){
            throw new IllegalArgumentException("找不到工具："+toolName);
        }

        //从Map的工具里取对应的方法，存进数组里
        Method method=def.getMethod();
        Class<?>[] paramsTypes=method.getParameterTypes();
//对象数组，存储方法的参数，长度和上面类型数组的长度一样

        Object[] args=new Object[paramsTypes.length];

        //判断是否只有一个String参数
        if (paramsTypes.length==1&& paramsTypes[0]==String.class){
//如果是，就把参数放进去
            args[0] = paramsPart;
        }else {
            throw new UnsupportedOperationException("暂时不支持多参数工具");
        }

        //4.执行天气工具的getweather方法
        Object result=method.invoke(def.getBean(),args);

        //5.
        return result==null?"换行成功，无返回值": result.toString();

    }
}

