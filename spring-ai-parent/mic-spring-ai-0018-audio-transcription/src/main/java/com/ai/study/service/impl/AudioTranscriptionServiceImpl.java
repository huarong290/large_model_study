package com.ai.study.service.impl;

import com.ai.study.model.TranscriptionResult;
import com.ai.study.service.AudioTranscriptionService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 语音转文字服务实现类
 *
 * @author AI Study
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AudioTranscriptionServiceImpl implements AudioTranscriptionService {

    /**
     * OpenAI 音频转录模型（Whisper）
     */
    private final OpenAiAudioTranscriptionModel transcriptionModel;

    @Value("${audio.transcription.default-language:zh}")
    private String defaultLanguage;

    @Value("${audio.transcription.supported-formats:mp3,wav,m4a,flac,ogg,webm}")
    private List<String> supportedFormats;

    @Value("${audio.transcription.max-file-size:25}")
    private int maxFileSizeMb;

    /**
     * 等待 @Value 属性注入完成后再进行日志打印
     */
    @PostConstruct
    public void initLog() {
        log.info("✅ 音频转录服务初始化完成");
        log.info("   📍 默认语言：{}", defaultLanguage);
        log.info("   📍 支持格式：{}", supportedFormats);
        log.info("   📍 最大文件：{}MB", maxFileSizeMb);
    }

    // ================================================================
    // 1. 单文件转录接口重定向（保持 DRY 原则）
    // ================================================================

    @Override
    public TranscriptionResult transcribe(MultipartFile file) {
        return transcribe(file, defaultLanguage, null);
    }

    @Override
    public TranscriptionResult transcribe(MultipartFile file, String language) {
        return transcribe(file, language, null);
    }

    // ================================================================
    // 2. 核心高级转录实现
    // ================================================================

    @Override
    public TranscriptionResult transcribe(MultipartFile file, String language, String prompt) {
        String fileName = (file != null) ? file.getOriginalFilename() : "unknown";
        log.info("🎤 开始转录音频：{}，语言：{}，Prompt：{}", fileName, language, prompt);

        // 1. 校验文件
        try {
            validateFile(file);
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ 文件校验失败：{}", e.getMessage());
            return TranscriptionResult.builder()
                    .success(false)
                    .error(e.getMessage())
                    .fileName(fileName)
                    .processedAt(LocalDateTime.now())
                    .build();
        }

        // 2. 确定语言
        String lang = StringUtils.hasText(language) ? language : defaultLanguage;

        // 3. 构建转录选项
        OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
                .model(OpenAiAudioApi.WhisperModel.WHISPER_1.getValue())
                .language(lang)
                .prompt(prompt)
                .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                .temperature(0.0f)
                .build();

        try {
            // 4. 执行转录（使用 file.getResource() 流式读取，避免全部加载到 byte[] 堆内存）
            AudioTranscriptionResponse response = transcriptionModel.call(
                    new AudioTranscriptionPrompt(file.getResource(), options)
            );

            String text = response.getResult().getOutput();

            log.info("✅ 转录完成：{}，长度：{} 字", fileName, text != null ? text.length() : 0);

            // 5. 构建结果
            return TranscriptionResult.builder()
                    .success(true)
                    .text(text)
                    .language(lang)
                    .fileName(fileName)
                    .fileSize(file.getSize())
                    .model("whisper-1")
                    .processedAt(LocalDateTime.now())
                    .hasTimestamps(false)
                    .build();

        } catch (Exception e) {
            log.error("❌ 语音识别失败：{}", e.getMessage(), e);
            return TranscriptionResult.builder()
                    .success(false)
                    .error("语音识别失败：" + e.getMessage())
                    .fileName(fileName)
                    .processedAt(LocalDateTime.now())
                    .build();
        }
    }

    // ================================================================
    // 3. 批量转录
    // ================================================================

    @Override
    public List<TranscriptionResult> batchTranscribe(List<MultipartFile> files, String language) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }

        log.info("📦 批量转录音频，共 {} 个文件", files.size());

        List<TranscriptionResult> results = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        for (MultipartFile file : files) {
            TranscriptionResult result = transcribe(file, language);
            results.add(result);
            if (result.isSuccess()) {
                successCount++;
            } else {
                failCount++;
            }
        }

        log.info("📦 批量转录完成，成功 {} 个，失败 {} 个", successCount, failCount);
        return results;
    }

    @Override
    public List<String> getSupportedFormats() {
        return supportedFormats;
    }

    // ================================================================
    // 4. 私有校验逻辑
    // ================================================================

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("音频文件不能为空");
        }

        long maxSize = maxFileSizeMb * 1024L * 1024L;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException(
                    String.format("文件大小超过限制：最大 %dMB，当前 %.2fMB",
                            maxFileSizeMb, file.getSize() / (1024.0 * 1024.0))
            );
        }

        String fileName = file.getOriginalFilename();
        if (!StringUtils.hasText(fileName) || !fileName.contains(".")) {
            throw new IllegalArgumentException("无法识别文件格式，请确保文件名包含扩展名");
        }

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        if (!supportedFormats.contains(extension)) {
            throw new IllegalArgumentException(
                    String.format("不支持的音频格式：%s，支持格式：%s",
                            extension, String.join(", ", supportedFormats))
            );
        }
    }
}