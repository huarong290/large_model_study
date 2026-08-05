┌─────────────────────────────────────────────────────────────────────┐
│                mic-spring-ai-0017-chat-client-practice             │
│                        综合实战 - 智能客服系统                      │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  功能整合：                                                         │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  1. 多模型支持    - Ollama + DeepSeek 自动切换              │    │
│  │  2. 对话记忆      - 多轮对话上下文保持                      │    │
│  │  3. RAG 增强      - 知识库检索增强生成                      │    │
│  │  4. 工具调用      - 天气查询、计算器等                      │    │
│  │  5. 缓存优化      - 提高响应速度                           │    │
│  │  6. 安全控制      - API Key + 敏感词过滤                   │    │
│  │  7. 效果评估      - 回答质量自动评分                       │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                     │
│  业务场景：                                                         │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  智能客服系统：                                               │    │
│  │  - 用户咨询 → 自动回答                                      │    │
│  │  - 多轮对话 → 保持上下文                                    │    │
│  │  - 知识库问答 → 基于产品文档                                │    │
│  │  - 工具调用 → 查询订单、天气等                              │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘


# 1. 智能对话
curl -X POST "http://localhost:12117/api/practice/chat" \
-H "Content-Type: application/json" \
-d '{"sessionId":"user-001","message":"你好"}'

# 2. 多轮对话
curl -X POST "http://localhost:12117/api/practice/chat" \
-H "Content-Type: application/json" \
-d '{"sessionId":"user-001","message":"我叫张三"}'
curl -X POST "http://localhost:12117/api/practice/chat" \
-H "Content-Type: application/json" \
-d '{"sessionId":"user-001","message":"我叫什么名字？"}'

# 3. 流式对话
curl -N "http://localhost:12117/api/practice/chat/stream?sessionId=user-001&message=讲个故事"

# 4. 工具调用
curl -X POST "http://localhost:12117/api/practice/chat" \
-H "Content-Type: application/json" \
-d '{"sessionId":"user-001","message":"北京天气怎么样？"}'

# 5. 系统统计
curl "http://localhost:12117/api/practice/stats"

# 6. 健康检查
curl "http://localhost:12117/api/practice/health"