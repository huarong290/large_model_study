package com.ai.study.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 语音转写结果模型（适配 Whisper / Spring AI Audio）
 *
 * @author AI Study
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranscriptionResult {

    /**
     * 转录完整文本
     */
    private String text;

    /**
     * 识别或指定的语言（如 zh, en, ja）
     */
    private String language;

    /**
     * 是否包含时间戳切片
     */
    private boolean hasTimestamps;

    /**
     * 文本时间轴切片列表（用于生成字幕或对齐音频）
     */
    private List<Segment> segments;

    /**
     * 原始音频文件名
     */
    private String fileName;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 音频播放时长（秒）
     */
    private Double duration;

    /**
     * 接口处理耗时（毫秒）
     */
    private Long latencyMs;

    /**
     * 处理完成时间
     */
    private LocalDateTime processedAt;

    /**
     * 调用的底层模型名称（如 whisper-1, whisper-large-v3）
     */
    private String model;

    /**
     * 是否转写成功
     */
    private boolean success;

    /**
     * 错误信息（失败时填充）
     */
    private String error;

    /**
     * 音频文本时间轴切片
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Segment {
        /**
         * 切片开始时间（秒）
         */
        private Double start;

        /**
         * 切片结束时间（秒）
         */
        private Double end;

        /**
         * 当前时间切片内的文本内容
         */
        private String text;
    }
}