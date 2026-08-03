package com.ai.study.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.UUID;

/**
 * TraceId 拦截器
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>在请求入口生成 TraceId</li>
 *   <li>存入 MDC，方便全链路日志追踪</li>
 *   <li>响应完成后自动清理</li>
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Component
public class TraceIdInterceptor implements HandlerInterceptor {

    private static final String TRACE_ID = "traceId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1. 尝试从请求头获取 TraceId（支持外部传入）
        String traceId = request.getHeader("X-Trace-Id");

        // 2. 如果没有传入，生成新的 TraceId
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString();
        }

        // 3. 存入 MDC
        MDC.put(TRACE_ID, traceId);

        // 4. 放入请求属性，方便后续获取
        request.setAttribute(TRACE_ID, traceId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清理 MDC，避免内存泄漏
        MDC.remove(TRACE_ID);
    }
}