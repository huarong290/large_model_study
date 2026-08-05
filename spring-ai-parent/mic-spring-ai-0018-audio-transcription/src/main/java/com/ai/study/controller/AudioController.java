package com.ai.study.controller;

import com.ai.study.model.TranscriptionResult;
import com.ai.study.service.AudioTranscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 音频转录 Controller
 *
 * @author AI Study
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/audio")
@Slf4j
@RequiredArgsConstructor
public class AudioController {

    private final AudioTranscriptionService transcriptionService;

    // ================================================================
    // 1. 单文件转录（支持 Prompt 专有名词引导）
    // ================================================================

    /**
     * 语音转文字
     *
     * @param file 音频文件
     * @param language 语言（可选，如 zh, en）
     * @param prompt 提示词（可选，用于指引专有名词识别，如 "Spring Boot, Docker, RAG"）
     * @return 转录结果
     */
    @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public TranscriptionResult transcribe(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "language", required = false) String language,
            @RequestParam(value = "prompt", required = false) String prompt) {

        log.info("📝 收到转录请求：文件={}，语言={}，Prompt={}",
                file != null ? file.getOriginalFilename() : "null", language, prompt);

        return transcriptionService.transcribe(file, language, prompt);
    }

    // ================================================================
    // 2. 批量转录
    // ================================================================

    /**
     * 批量语音转文字
     *
     * @param files 音频文件列表
     * @param language 语言（可选）
     * @return 转录结果列表
     */
    @PostMapping(value = "/transcribe/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<TranscriptionResult> batchTranscribe(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "language", required = false) String language) {

        int fileCount = (files != null) ? files.size() : 0;
        log.info("📦 收到批量转录请求：{} 个文件", fileCount);

        return transcriptionService.batchTranscribe(files, language);
    }

    // ================================================================
    // 3. 获取支持格式
    // ================================================================

    @GetMapping("/formats")
    public Map<String, Object> getSupportedFormats() {
        Map<String, Object> result = new HashMap<>();
        result.put("formats", transcriptionService.getSupportedFormats());
        result.put("maxFileSizeMB", 25);
        result.put("supportedLanguages", List.of("zh", "en", "ja", "ko", "fr", "de", "es"));
        return result;
    }

    // ================================================================
    // 4. 健康检查
    // ================================================================

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "语音转文字服务");
        status.put("version", "1.0.0");
        return status;
    }
}