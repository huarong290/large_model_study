package com.ai.study.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 处理安全相关异常 (401 / 403 / 429)
     */
    @ExceptionHandler(com.ai.study.exception.SecurityException.class)
    public ResponseEntity<Map<String, Object>> handleSecurityException(SecurityException e) {
        log.warn("⚠️ 安全异常：{}，错误码：{}", e.getMessage(), e.getErrorCode());
        HttpStatus status = switch (e.getErrorCode()) {
            case "INVALID_API_KEY", "MISSING_API_KEY", "UNAUTHORIZED" -> HttpStatus.UNAUTHORIZED;
            case "RATE_LIMIT_EXCEEDED" -> HttpStatus.TOO_MANY_REQUESTS;
            case "SENSITIVE_WORD_DETECTED" -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.BAD_REQUEST;
        };

        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("error", e.getMessage());
        body.put("code", e.getErrorCode());
        body.put("timestamp", LocalDateTime.now().toString());

        return buildResponse(status, e.getErrorCode(), e.getMessage());
    }
    /**
     * 处理业务参数非法异常 (400)
     * 对应 Service 中 validateFile 抛出的 IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("⚠️ 参数检验失败：{}", e.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER", e.getMessage());
    }
    /**
     * 处理 Spring 文件大小超过 max-request-size 限制的异常 (400)
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.warn("⚠️ 上传文件超出系统最大限制：{}", e.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "FILE_TOO_LARGE", "上传文件大小超出系统允许的最大限制");
    }
    /**
     * 处理系统未知异常 (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        log.error("❌ 系统异常：{}", e.getMessage(), e);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "系统内部错误：" + e.getMessage());
    }
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String code, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("code", code);
        body.put("error", message);
        body.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.status(status).body(body);
    }
}
