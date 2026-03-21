package com.example.agent.tool;


import org.springframework.stereotype.Component;

@Component
public class WeatherTool {
    @Tool(name = "get_weather",description = "获取某个城市的天气情况，参数格式：city=城市名")
   public String getWeather(String params){
        //从参数中提取城市名
       String city="未知城市";
       if (params.contains("city=")){
           String[] parts = params.split("city=");
           // 增加数组长度判断，避免越界
           if (parts.length >= 2) {
               city = parts[1];
           }

       }
       if (city.contains("北京")){
           return "北京天气：晴，25℃，空气质量优。";
       } else if (city.contains("上海")) {
           return "上海天气：多云，28℃，空气质量良。";
       }else {
           return "暂未获取到" + city + "的天气信息。";
       }
   }




}
