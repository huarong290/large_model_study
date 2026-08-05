package com.ai.study.exception;

/**
 * 安全异常
 *
 * @author AI Study
 * @since 1.0.0
 */
public class SecurityException extends RuntimeException {

    private final String errorCode;

    public SecurityException(String message) {
        super(message);
        this.errorCode = "SECURITY_ERROR";
    }

    public SecurityException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public SecurityException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "SECURITY_ERROR";
    }

    public String getErrorCode() {
        return errorCode;
    }

    /**
     * API Key 无效异常
     */
    public static SecurityException invalidApiKey() {
        return new SecurityException("无效的 API Key，请检查 X-API-Key 请求头", "INVALID_API_KEY");
    }

    /**
     * API Key 缺失异常
     */
    public static SecurityException missingApiKey() {
        return new SecurityException("缺少 API Key，请在请求头中添加 X-API-Key", "MISSING_API_KEY");
    }

    /**
     * 限流异常
     */
    public static SecurityException rateLimitExceeded() {
        return new SecurityException("请求过于频繁，请稍后再试", "RATE_LIMIT_EXCEEDED");
    }

    /**
     * 敏感词异常
     */
    public static SecurityException sensitiveWordDetected(String word) {
        return new SecurityException("检测到敏感词：" + word, "SENSITIVE_WORD_DETECTED");
    }

    /**
     * 未授权异常
     *
     */
    public static SecurityException unauthorized(String msg) {
        return new SecurityException(msg, "UNAUTHORIZED");
    }
}