┌─────────────────────────────────────────────────────────────────────┐
│                 mic-spring-ai-0010-chat-client-tools               │
│                        工具调用（Function Calling）                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  核心问题：                                                         │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  ❌ 没有工具调用：                                           │    │
│  │  用户：北京今天天气怎么样？                                  │    │
│  │  AI：我不知道，因为我没有实时数据...                        │    │
│  │                                                             │    │
│  │  ✅ 有工具调用：                                            │    │
│  │  用户：北京今天天气怎么样？                                  │    │
│  │  AI：调用 getWeather("北京") → 返回 "25°C 晴"              │    │
│  │  AI：北京今天天气晴朗，温度25°C。                          │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                     │
│  工作流程：                                                         │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  用户提问 → AI 判断需要工具 → 调用工具 → 获取结果 → 生成回答 │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                     │
│  核心组件：                                                         │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  @Tool 注解   - 标记工具方法                                │    │
│  │  .tools()     - 注册工具到 ChatClient                       │    │
│  │  FunctionCalling - 工具自动调用机制                         │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
核心知识点
概念	                说明
@Tool	            标记方法为可被 AI 调用的工具
@ToolParam	        描述工具参数，帮助 AI 理解
.tools()	        注册工具到 ChatClient
Function Calling	AI 自动判断并调用工具
多工具协同	        AI 可以组合使用多个工具


# ================================================================
# 1. 带工具调用的对话
# ================================================================

# 天气查询
curl -X POST "http://localhost:12110/api/tools/chat" \
-H "Content-Type: application/json" \
-d '{"message": "北京今天天气怎么样？"}'

# 数学计算
curl -X POST "http://localhost:12110/api/tools/chat" \
-H "Content-Type: application/json" \
-d '{"message": "计算 3.14 * 2 的平方"}'

# 时间查询
curl -X POST "http://localhost:12110/api/tools/chat" \
-H "Content-Type: application/json" \
-d '{"message": "现在几点了？"}'

# 知识搜索
curl -X POST "http://localhost:12110/api/tools/chat" \
-H "Content-Type: application/json" \
-d '{"message": "介绍一下 Spring AI"}'

# 多工具协同
curl -X POST "http://localhost:12110/api/tools/chat" \
-H "Content-Type: application/json" \
-d '{"message": "北京今天天气怎么样？然后计算 100 + 200"}'

# ================================================================
# 2. 对比测试
# ================================================================

curl -X POST "http://localhost:12110/api/tools/compare" \
-H "Content-Type: application/json" \
-d '{"message": "北京今天天气怎么样？"}'

# ================================================================
# 3. 单工具测试
# ================================================================

# 天气
curl -X POST "http://localhost:12110/api/tools/weather" \
-H "Content-Type: application/json" \
-d '{"city": "上海"}'

# 计算
curl -X POST "http://localhost:12110/api/tools/calculate" \
-H "Content-Type: application/json" \
-d '{"expression": "(1 + 2) * 3"}'

# 时间
curl -X POST "http://localhost:12110/api/tools/time" \
-H "Content-Type: application/json" \
-d '{"city": "东京"}'

# 搜索
curl -X POST "http://localhost:12110/api/tools/search" \
-H "Content-Type: application/json" \
-d '{"query": "RAG", "maxResults": 2}'

# 系统
curl -X POST "http://localhost:12110/api/tools/system" \
-H "Content-Type: application/json" \
-d '{"type": "memory"}'

# ================================================================
# 4. 工具列表
# ================================================================

curl "http://localhost:12110/api/tools/list"