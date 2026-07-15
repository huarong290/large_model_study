#   一、SpringAI介绍
## 1.1 什么是Spring AI
    Spring AI 是面向JAVA和Spring生态的原生生成式人工智能框架。
    Spring AI 倡导 一套接口，多种实现，，开发者无需为不同AI提供商逐一适配，而是可以通过统一抽象实现轻松切换，比如OpenAI、Anthropic、Bedrock、huagging face、Vertex AI、Ollana等服务
    
    Spring AI 官网地址：https://spring.io/projects/spring-ai
    Spring AI 文档地址：https://docs.spring.io/spring-ai/reference/index.html
    Spring AI 中文文档地址：https://spring-ai.spring-doc.cn/docs/2.0.0-SNAPSHOT/index.html

## 1.2 Spring AI 特点
    springAI 功能模块丰富，涵盖AI应用开发的各个环节，具备如下特点：
### 1.2.1 多供应商模型支持
    支持主流的AI模型提供商 如Anthropic、Open AI、Microsoft、Amazon、Google、Ollama等模型服务。
    通过这些模型可以实现聊天、文本嵌入、文生图、影评转录、文本转语音、内容审核等多种能力
### 1.2.2 统一抽象API
    提供如ChatClient,EmbeddingModel,ImageModel等统一接口，无论切换到哪家AI平台，
    调用方式一致同时支持同步与流式调用，也能够访问模型特定功能。
### 1.2.3 springboot集成
    以starter和自动装配方式支持AI模型、向量数据库、ETL工具等，开发这可通过Initializr快速上手

### 1.2.4 结构化输出与类型安全
    模型 等响应可解析并映射到JAVA POJO，保证后续处理到类型安全与可维护性
### 1.2.5 向量存储与RAG
    集成了主流向量数据库(PostqreSQL/pqvector、Pinecone、Qdrant、Qdrant、Redis、Weaviate等)及其元数据过滤，通过内置
    的ETL文档处理流程，结合检索增强生成实现文档问答和聊天检索
### 1.2.6 Tool/Function Calling
    支持模型发起函数调用(类似于OpenAI Function Calling),可以注册Spring Bean作为可调用工具，从而访问实时业务系统或执行外部操作
### 1.2.7 可观测性与评估
    内建对于AI调用的监控指标与日志、模型评估工具，可用户检测响应准确性、防止"幻觉"

## 1.3 Spring AI 快速上手

### 1.3.1 环境要求
    Spring AI 构建在Spring Boot3.x之上，Spring Boot3.x系列最低JAVA要求版本是JDK17，不支持JAVA8/11/16等低于17的版本，推荐使用
    Maven3.6及其以上版本。
    我们后续使用Spring AI时，对应环境版本如下：
    Springboot3.5.0版本
    JDK17版本
    Maven3.9.9版本

#   二、Model模型

## 2.1 ChatModel

## 2.2 Embedding Models
## 2.2.1 Embedding介绍
    Embedding是一种讲文本转换成数字向量的技术，这些向量表示了输入内容在语义空间中的位置，能够反映它们之间的相似度--向量距离越近，内容越相似。
    Spring AI 通过EmbeddingModel 接口提供一套统一、简单、可替换的访问方式，支持多种底层模型，这样可以统一接口，切换模型金需要改配置，不用该调用逻辑。
    Spring AI 中的Embedding 使用场景如下：
        相似度计算/语义搜索：将查询和文档全部转换为向量，构建向量数据库进行近邻检索
        想聚与分类：将文本转换为向量后，使用传统算法进行聚类或分类
        检索增强生成(RAG):先用向量搜索获取相关知识，再结合生成模型回答
        推荐系统：如问答推荐、内容推荐
        异常检测：语义异常内容检测
## 2.2.2 智谱AI Embedding 使用示例
    智谱AI(北京智谱华章科技有限公司)是清华大学知识工程实验室成果转化成立的大模型研发公司，专注于构建 认知智能体系。其产品包括文字对话模型、图像乃至视频理解模型以及文本视频生成模型等，
    覆盖文字、图像、音视频等多种模态。
    使用智谱AI需要以下网站进行注册并充值，智谱AI相关网站如下
        智谱AI官网地址: http://open.bigmodel.cn/
        智谱AI key地址: http://open.bigmodel.cn/
        智谱AI充值地址
        智谱AI相关模型费用地址
        
    