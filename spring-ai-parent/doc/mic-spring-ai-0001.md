# 1. 健康检查
curl "http://localhost:12101/api/chat/health"

# 2. Ollama 对话
curl "http://localhost:12101/api/chat/default?message=你好"

# 3. DeepSeek 对话（需要配置 API Key）
curl "http://localhost:12101/api/chat/deepseek?message=你好"

# 4. Gemini 对话（需要配置 API Key）
curl "http://localhost:12101/api/chat/gemini?message=你好"

# 5. 对比所有模型
curl "http://localhost:12101/api/chat/compare?message=你好"

# 6. 带 System 消息
curl "http://localhost:12101/api/chat/with-system?message=什么是AI&system=你是一个编程专家"

# 7. 流式响应
curl "http://localhost:12101/api/chat/stream?message=讲个故事"