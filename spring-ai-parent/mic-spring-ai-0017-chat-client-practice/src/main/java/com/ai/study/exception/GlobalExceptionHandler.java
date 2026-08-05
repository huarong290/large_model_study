package com.ai.study.exception;



import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SecurityException.class)
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

        return ResponseEntity.status(status).body(body);
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        log.error("❌ 系统异常：{}", e.getMessage(), e);

        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("error", "系统内部错误：" + e.getMessage());
        body.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
