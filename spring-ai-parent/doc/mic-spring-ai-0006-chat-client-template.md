┌─────────────────────────────────────────────────────────────────────┐
│              mic-spring-ai-0006-chat-client-template               │
│                      PromptTemplate 参数化                         │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  学习目标：                                                         │
│  1. 理解 PromptTemplate 的作用和优势                               │
│  2. 掌握位置占位符 {0}, {1}, {2} 的使用                            │
│  3. 掌握命名占位符 {name}, {age} 的使用                            │
│  4. 掌握动态构建 Prompt 的方法                                     │
│  5. 实现批量参数化测试                                              │
│  6. 实现模板缓存和复用                                              │
│                                                                     │
│  核心概念：                                                         │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  占位符类型  │ 示例              │ 适用场景                 │    │
│  ├──────────────┼───────────────────┼──────────────────────────┤    │
│  │  位置占位符  │ {0}, {1}, {2}    │ 参数少且顺序固定的场景    │    │
│  │  命名占位符  │ {name}, {city}   │ 参数多或需要语义清晰的    │    │
│  │  混合使用    │ {0} + {name}     │ 复杂场景                  │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                     │
│  模板优势：                                                         │
│  1. 复用性强 - 一个模板多次使用                                    │
│  2. 维护方便 - 修改一处全局生效                                    │
│  3. 参数验证 - 可以校验参数完整性                                  │
│  4. 易于测试 - 模板可以单独测试                                    │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘

# ================================================================
# 1. 模板管理（GET）
# ================================================================

# 获取所有模板
curl "http://localhost:12106/api/template/list"

# 获取模板详情
curl "http://localhost:12106/api/template/introduce"


# ================================================================
# 2. 命名占位符模板（POST）
# ================================================================

# 自我介绍
curl -X POST "http://localhost:12106/api/template/named/introduce" \
-H "Content-Type: application/json" \
-d '{"name":"小智","age":"25","city":"北京","hobby":"编程、阅读"}'

# 角色扮演
curl -X POST "http://localhost:12106/api/template/named/role_play" \
-H "Content-Type: application/json" \
-d '{"role":"程序员","scenario":"面试","tone":"自信专业"}'

# 知识问答
curl -X POST "http://localhost:12106/api/template/named/knowledge" \
-H "Content-Type: application/json" \
-d '{"topic":"Spring AI","level":"初学者","detail":"基本概念和使用方法"}'


# ================================================================
# 3. 位置占位符模板（POST）
# ================================================================

# 翻译
curl -X POST "http://localhost:12106/api/template/positional/translate" \
-H "Content-Type: application/json" \
-d '{"params":["Hello, world!","中文","正式"]}'

# 总结
curl -X POST "http://localhost:12106/api/template/positional/summarize" \
-H "Content-Type: application/json" \
-d '{"params":["人工智能是计算机科学的一个分支，致力于创建能够执行通常需要人类智能的任务的系统。","50","核心定义"]}'

# 代码生成
curl -X POST "http://localhost:12106/api/template/positional/code" \
-H "Content-Type: application/json" \
-d '{"params":["Java","冒泡排序","考虑性能优化"]}'


# ================================================================
# 4. 快捷接口（GET）
# ================================================================

# 自我介绍
curl "http://localhost:12106/api/template/introduce?name=小明&age=30&city=上海&hobby=音乐"

# 角色扮演
curl "http://localhost:12106/api/template/role-play?role=老师&scenario=上课&tone=亲切"

# 知识问答
curl "http://localhost:12106/api/template/knowledge?topic=深度学习&level=进阶&detail=卷积神经网络"

# 翻译
curl "http://localhost:12106/api/template/translate?text=Good%20morning&target=中文&style=友好"

# 总结
curl "http://localhost:12106/api/template/summarize?text=人工智能是计算机科学的一个分支...&limit=50&focus=核心概念"

# 代码生成
curl "http://localhost:12106/api/template/code?language=Python&function=快速排序&requirement=代码注释完整"


# ================================================================
# 5. 批量测试
# ================================================================

curl "http://localhost:12106/api/template/batch-test"