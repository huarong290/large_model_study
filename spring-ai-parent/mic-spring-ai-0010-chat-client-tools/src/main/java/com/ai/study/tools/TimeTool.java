package com.ai.study.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 时间工具
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>获取当前时间</li>
 *   <li>支持不同时区</li>
 *   <li>支持时间格式化</li>
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Component
@Slf4j
public class TimeTool {

    /**
     * 时区映射
     */
    private static final Map<String, String> TIMEZONE_MAP = new HashMap<>();

    static {
        TIMEZONE_MAP.put("北京", "Asia/Shanghai");
        TIMEZONE_MAP.put("上海", "Asia/Shanghai");
        TIMEZONE_MAP.put("东京", "Asia/Tokyo");
        TIMEZONE_MAP.put("纽约", "America/New_York");
        TIMEZONE_MAP.put("伦敦", "Europe/London");
        TIMEZONE_MAP.put("巴黎", "Europe/Paris");
        TIMEZONE_MAP.put("悉尼", "Australia/Sydney");
        TIMEZONE_MAP.put("新加坡", "Asia/Singapore");
    }

    /**
     * 获取当前时间
     *
     * @param city 城市名称（可选）
     * @param format 时间格式（可选）
     * @return 当前时间
     */
    @Tool(name = "getCurrentTime", description = "获取当前时间，支持不同时区和自定义格式")
    public String getCurrentTime(
            @ToolParam(description = "城市名称，用于确定时区，如：北京、纽约、伦敦", required = false) String city,
            @ToolParam(description = "时间格式，如：yyyy-MM-dd HH:mm:ss", required = false) String format) {

        log.info("🕐 调用时间工具：city={}, format={}", city, format);

        // 确定时区
        ZoneId zoneId = ZoneId.systemDefault();
        if (city != null && !city.trim().isEmpty()) {
            String timezoneId = TIMEZONE_MAP.get(city);
            if (timezoneId != null) {
                zoneId = ZoneId.of(timezoneId);
            }
        }

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now(zoneId);

        // 格式化
        String pattern = (format != null && !format.trim().isEmpty())
                ? format : "yyyy-MM-dd HH:mm:ss";

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            String formattedTime = now.format(formatter);

            String result = String.format("当前时间：%s（时区：%s）",
                    formattedTime, zoneId.getId());

            if (city != null && !city.trim().isEmpty()) {
                result = String.format("城市：%s，%s", city, result);
            }

            return result;

        } catch (Exception e) {
            log.error("时间格式化失败：{}", e.getMessage());
            return String.format("时间格式化失败：%s，请检查格式", e.getMessage());
        }
    }
}