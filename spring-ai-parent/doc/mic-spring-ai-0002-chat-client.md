# ================================================================
# 📌 通用接口测试
# ================================================================

# 1. 健康检查
curl "http://localhost:12102/api/chat/health"

# 2. 默认模型（Ollama）对话
curl "http://localhost:12102/api/chat/default?message=你好"

# 3. DeepSeek 对话
curl "http://localhost:12102/api/chat/deepseek?message=你好"

# 4. Gemini 对话
curl "http://localhost:12102/api/chat/gemini?message=你好"

# 5. 多模型对比
curl "http://localhost:12102/api/chat/compare?message=你好"

# 6. 带 System 消息
curl "http://localhost:12102/api/chat/with-system?message=什么是AI&system=你是一个编程专家"

# ================================================================
# 🎯 本模块特有接口测试
# ================================================================

# 7. 基础调用链
curl "http://localhost:12102/api/chat/simple?message=你好"

# 8. Message 对象方式
curl -X POST "http://localhost:12102/api/chat/messages" \
-H "Content-Type: application/json" \
-d '{"user":"你好","system":"你是一个友好的AI助手"}'

# 9. 角色测试
curl "http://localhost:12102/api/chat/role?role=poet&message=写一首关于春天的诗"

# 10. 流式响应
curl "http://localhost:12102/api/chat/stream?message=讲个故事"


┌─────────────────────────────────────────────────────────────────┐
│                    ChatController（合并版）                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ 📌 通用接口（mic-spring-ai-0001 已有，直接继承）          │  │
│  ├──────────────────────────────────────────────────────────┤  │
│  │  /health      健康检查                                   │  │
│  │  /default     默认模型对话                               │  │
│  │  /deepseek    DeepSeek 对话                             │  │
│  │  /gemini      Gemini 对话                               │  │
│  │  /compare     多模型对比                                 │  │
│  │  /with-system 带 System 消息                            │  │
│  └──────────────────────────────────────────────────────────┘  │
│                              ↓                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ 🎯 本模块特有接口（mic-spring-ai-0002 新增）              │  │
│  ├──────────────────────────────────────────────────────────┤  │
│  │  /simple      基础调用链演示                             │  │
│  │  /messages    Message 对象构建 Prompt                    │  │
│  │  /role        不同角色 System 提示词                     │  │
│  │  /stream      流式响应演示                               │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘