package com.ai.study.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * API Key 过滤器
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>验证请求头中的 X-API-Key</li>
 *   <li>拦截未认证的请求</li>
 *   <li>支持多 Key 配置</li>
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Slf4j
public class ApiKeyFilter implements Filter {

    /**
     * API Key 请求头名称
     */
    private static final String API_KEY_HEADER = "X-API-Key";

    /**
     * 是否启用 API Key 认证
     */
    private final boolean enabled;

    /**
     * 有效的 API Key 集合
     */
    private final Set<String> validKeys;

    /**
     * ✅ 白名单路径（从配置文件注入）
     */
    private final List<String> whitelistPaths;

    public ApiKeyFilter(boolean enabled, Set<String> validKeys,List<String> whitelistPaths) {
        this.enabled = enabled;
        this.validKeys = validKeys;
        this.whitelistPaths = whitelistPaths;
        log.info("🔑 ApiKeyFilter 初始化，启用状态：{}，有效 Key 数量：{}，白名单路径：{}",
                enabled, validKeys.size(), whitelistPaths);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 如果未启用，直接放行
        if (!enabled) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 获取请求路径
        String path = httpRequest.getRequestURI();

        // 放行健康检查、Actuator 监控以及无安全控制对比测试接口
        // ✅ 修复：从配置读取白名单路径
        if (isWhitelisted(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 获取 API Key
        String apiKey = httpRequest.getHeader(API_KEY_HEADER);

        // 验证 API Key
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("🔑 拒绝访问：请求缺少 API Key，来源 IP：{}", httpRequest.getRemoteAddr());
            renderErrorResponse(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "MISSING_API_KEY", "缺少 API Key，请在请求头中添加 X-API-Key");
            return;
        }

        if (!validKeys.contains(apiKey)) {
            log.warn("🔑 拒绝访问：无效的 API Key [{}]，来源 IP：{}", apiKey, httpRequest.getRemoteAddr());
            renderErrorResponse(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "INVALID_API_KEY", "无效的 API Key");
            return;
        }

        // 验证通过，将 API Key 存入请求属性
        httpRequest.setAttribute("apiKey", apiKey);
        log.debug("🔑 API Key 验证通过：{}", apiKey);

        chain.doFilter(request, response);
    }

    /**
     *  判断是否在白名单中
     */
    private boolean isWhitelisted(String path) {
        if (whitelistPaths == null || whitelistPaths.isEmpty()) {
            return false;
        }
        return whitelistPaths.stream().anyMatch(path::startsWith);
    }
    /**
     *  修改后：统一 JSON 返回格式
     */
    private void renderErrorResponse(HttpServletResponse response, int status, String code, String msg) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");

        // 拼装规范的 JSON 结构
        String json = String.format(
                "{\"success\":false,\"code\":\"%s\",\"error\":\"%s\"}",
                code, msg
        );

        response.getWriter().write(json);
    }
}