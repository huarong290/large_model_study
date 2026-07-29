package com.ai.study.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 模型指标服务
 *
 * 功能说明：
 * 1. 统计各模型的调用次数
 * 2. 统计各模型的响应时间
 * 3. 统计各模型的成功率
 *
 * @author AI Study
 */
@Service
@Slf4j
public class ModelMetricsService {

    /**
     * 模型统计信息
     */
    @Data
    public static class ModelStats {
        private final AtomicLong totalCalls = new AtomicLong(0);
        private final AtomicLong successCalls = new AtomicLong(0);
        private final AtomicLong failCalls = new AtomicLong(0);
        private final AtomicLong totalResponseTime = new AtomicLong(0);
        private volatile long minResponseTime = Long.MAX_VALUE;
        private volatile long maxResponseTime = 0;

        /**
         * 记录一次成功调用
         */
        public void recordSuccess(long responseTime) {
            totalCalls.incrementAndGet();
            successCalls.incrementAndGet();
            totalResponseTime.addAndGet(responseTime);
            updateMinMax(responseTime);
        }

        /**
         * 记录一次失败调用
         */
        public void recordFailure() {
            totalCalls.incrementAndGet();
            failCalls.incrementAndGet();
        }

        private void updateMinMax(long time) {
            if (time < minResponseTime) {
                minResponseTime = time;
            }
            if (time > maxResponseTime) {
                maxResponseTime = time;
            }
        }

        /**
         * 获取平均响应时间
         */
        public double getAvgResponseTime() {
            long success = successCalls.get();
            return success == 0 ? 0 : (double) totalResponseTime.get() / success;
        }

        /**
         * 获取成功率
         */
        public double getSuccessRate() {
            long total = totalCalls.get();
            return total == 0 ? 0 : (double) successCalls.get() / total * 100;
        }
    }

    private final Map<String, ModelStats> statsMap = new ConcurrentHashMap<>();

    /**
     * 获取或创建模型统计
     */
    public ModelStats getOrCreateStats(String modelName) {
        return statsMap.computeIfAbsent(modelName, k -> new ModelStats());
    }

    /**
     * 记录成功调用
     */
    public void recordSuccess(String modelName, long responseTime) {
        ModelStats stats = getOrCreateStats(modelName);
        stats.recordSuccess(responseTime);
        log.debug("📊 模型 [{}] 调用成功，耗时 {}ms", modelName, responseTime);
    }

    /**
     * 记录失败调用
     */
    public void recordFailure(String modelName) {
        ModelStats stats = getOrCreateStats(modelName);
        stats.recordFailure();
        log.warn("📊 模型 [{}] 调用失败", modelName);
    }

    /**
     * 获取所有模型统计
     */
    public Map<String, ModelStats> getAllStats() {
        return statsMap;
    }

    /**
     * 获取指定模型统计
     */
    public ModelStats getStats(String modelName) {
        return statsMap.get(modelName);
    }

    /**
     * 重置统计
     */
    public void resetStats() {
        statsMap.clear();
        log.info("📊 所有模型统计已重置");
    }
}