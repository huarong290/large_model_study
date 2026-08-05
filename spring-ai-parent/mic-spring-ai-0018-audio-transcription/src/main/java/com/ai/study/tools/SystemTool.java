package com.ai.study.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;

/**
 * 系统信息工具
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>获取系统运行状态</li>
 *   <li>获取内存使用情况</li>
 *   <li>获取 JVM 信息</li>
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Component
@Slf4j
public class SystemTool {

    /**
     * 获取系统信息
     *
     * @param type 信息类型：all, memory, os, jvm
     * @return 系统信息
     */
    @Tool(name = "getSystemInfo", description = "获取系统运行状态信息，包括内存、操作系统、JVM 等")
    public String getSystemInfo(
            @ToolParam(description = "信息类型：all（全部）、memory（内存）、os（操作系统）、jvm（JVM）",
                    required = false) String type) {

        log.info("🖥️ 调用系统工具：type={}", type);

        String infoType = (type != null && !type.trim().isEmpty()) ? type : "all";
        StringBuilder result = new StringBuilder();

        switch (infoType.toLowerCase()) {
            case "all":
                result.append(getMemoryInfo());
                result.append("\n");
                result.append(getOsInfo());
                result.append("\n");
                result.append(getJvmInfo());
                break;
            case "memory":
                result.append(getMemoryInfo());
                break;
            case "os":
                result.append(getOsInfo());
                break;
            case "jvm":
                result.append(getJvmInfo());
                break;
            default:
                return String.format("不支持的信息类型：%s，支持：all, memory, os, jvm", type);
        }

        return result.toString();
    }

    /**
     * 获取内存信息
     */
    private String getMemoryInfo() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long maxMemory = Runtime.getRuntime().maxMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usedMemory = totalMemory - freeMemory;

        return String.format("""
                【内存信息】
                - 最大内存：%.2f MB
                - 已分配内存：%.2f MB
                - 已用内存：%.2f MB
                - 空闲内存：%.2f MB
                - 使用率：%.1f%%
                """,
                maxMemory / (1024.0 * 1024.0),
                totalMemory / (1024.0 * 1024.0),
                usedMemory / (1024.0 * 1024.0),
                freeMemory / (1024.0 * 1024.0),
                (double) usedMemory / totalMemory * 100
        );
    }

    /**
     * 获取操作系统信息
     */
    private String getOsInfo() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        return String.format("""
                【操作系统信息】
                - 系统名称：%s
                - 系统版本：%s
                - 架构：%s
                - CPU 核心数：%d
                - 系统负载：%.2f
                """,
                osBean.getName(),
                osBean.getVersion(),
                osBean.getArch(),
                osBean.getAvailableProcessors(),
                osBean.getSystemLoadAverage()
        );
    }

    /**
     * 获取 JVM 信息
     */
    private String getJvmInfo() {
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        return String.format("""
                【JVM 信息】
                - JVM 名称：%s
                - JVM 版本：%s
                - JVM 供应商：%s
                - 启动时间：%s
                - 运行时间：%d 秒
                """,
                runtimeBean.getVmName(),
                runtimeBean.getVmVersion(),
                runtimeBean.getVmVendor(),
                runtimeBean.getStartTime(),
                (System.currentTimeMillis() - runtimeBean.getStartTime()) / 1000
        );
    }
}