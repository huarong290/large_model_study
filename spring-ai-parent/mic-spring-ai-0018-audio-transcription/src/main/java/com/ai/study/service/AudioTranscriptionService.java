package com.ai.study.service;

import com.ai.study.model.TranscriptionResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 语音转文字服务接口
 *
 * @author AI Study
 * @since 1.0.0
 */
public interface AudioTranscriptionService {

    /**
     * 转录单个音频文件
     *
     * @param file 音频文件
     * @param language 语言代码（zh/en/ja等）
     * @return 转录结构化结果
     */
    TranscriptionResult transcribe(MultipartFile file, String language);

    /**
     * 转录单个音频文件（使用默认语言）
     *
     * @param file 音频文件
     * @return 转录结构化结果
     */
    TranscriptionResult transcribe(MultipartFile file);

    /**
     * 高级转录：支持领域 Prompt 引导（如专业术语、专有名词）及格式配置
     *
     * @param file 音频文件
     * @param language 语言代码
     * @param prompt 引导提示词（例如："以下是关于 MyBatis-Plus 和 Spring AI 的技术讨论"）
     * @return 转录结构化结果
     */
    TranscriptionResult transcribe(MultipartFile file, String language, String prompt);

    /**
     * 批量转录音频文件
     *
     * @param files 音频文件列表
     * @param language 语言代码
     * @return 转录结果列表
     */
    List<TranscriptionResult> batchTranscribe(List<MultipartFile> files, String language);

    /**
     * 获取系统支持的音频格式
     *
     * @return 支持的格式列表（如 mp3, wav, m4a, flac, webm 等）
     */
    List<String> getSupportedFormats();
}
