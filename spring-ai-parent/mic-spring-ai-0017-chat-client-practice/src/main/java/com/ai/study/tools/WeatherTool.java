package com.ai.study.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 天气查询工具
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>模拟查询指定城市的天气信息</li>
 *   <li>返回温度、天气状况、湿度等信息</li>
 *   <li>演示 @Tool 注解的基本使用</li>
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Component
@Slf4j
public class WeatherTool {

    /**
     * 模拟天气数据缓存
     */
    private static final Map<String, String[]> WEATHER_DATA = new HashMap<>();
    private static final Random RANDOM = new Random();

    static {
        // 初始化模拟天气数据
        WEATHER_DATA.put("北京", new String[]{"晴", "25", "30", "南风"});
        WEATHER_DATA.put("上海", new String[]{"多云", "28", "65", "东风"});
        WEATHER_DATA.put("广州", new String[]{"阵雨", "30", "80", "东南风"});
        WEATHER_DATA.put("深圳", new String[]{"晴", "32", "60", "西南风"});
        WEATHER_DATA.put("杭州", new String[]{"阴", "26", "70", "北风"});
        WEATHER_DATA.put("成都", new String[]{"多云", "24", "55", "东北风"});
        WEATHER_DATA.put("武汉", new String[]{"晴", "29", "45", "南风"});
        WEATHER_DATA.put("南京", new String[]{"小雨", "27", "75", "东风"});
        WEATHER_DATA.put("西安", new String[]{"晴", "23", "35", "西北风"});
        WEATHER_DATA.put("重庆", new String[]{"阴", "28", "65", "南风"});
    }

    /**
     * 查询天气
     *
     * <p>工具方法使用 @Tool 注解标记，AI 可以自动调用。</p>
     * <p>@ToolParam 注解描述参数，帮助 AI 理解如何传参。</p>
     *
     * @param city 城市名称
     * @return 天气信息
     */
    @Tool(name = "getWeather", description = "获取指定城市的实时天气信息，包括温度、天气状况、湿度和风力")
    public String getWeather(
            @ToolParam(description = "城市名称，如：北京、上海、广州")  String city) {

        log.info("start 调用天气工具：city={}", city);

        // 参数校验
        if (city == null || city.trim().isEmpty()) {
            return "请提供有效的城市名称";
        }

        // 查找天气数据
        String[] weather = WEATHER_DATA.get(city);
        String result;
        if (weather == null) {
            // 随机生成数据
            String[] conditions = {"晴", "多云", "阴", "小雨", "阵雨", "晴转多云"};
            String[] winds = {"南风", "北风", "东风", "西风", "东南风", "西北风"};
            String condition = conditions[RANDOM.nextInt(conditions.length)];
            int temp = 15 + RANDOM.nextInt(20);
            int humidity = 30 + RANDOM.nextInt(50);
            String wind = winds[RANDOM.nextInt(winds.length)];

            result = String.format("城市：%s，天气：%s，温度：%d°C，湿度：%d%%，风力：%s",
                    city, condition, temp, humidity, wind);
        }else {
            //  使用缓存数据
            result = String.format("城市：%s，天气：%s，温度：%s°C，湿度：%s%%，风力：%s",
                    city, weather[0], weather[1], weather[2], weather[3]);
        }
        log.info("end 调用天气工具：city={},result={}", city,result);
        return result;
    }
}