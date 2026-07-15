 ## langchain4j vs spring ai
 
                langchain4j                                   spring ai
生态          不依赖spring,需要单独集成sring                     spring官方和spring无缝集成
诞生          更早，中国团队，受langChain启发                     稍晚，但是明显后来居上
jdk         v0.35.0前版本之前jdk8,后支持jdk17                   全版本jdk17
功能         没有mcp server,官方建议使用quarkus-mcp-server       早期落后langchains4j,现在功能全面，并且生态活跃，开源贡献重多
易用性。     尚可，中文文档                                       易用，api优雅
最终         不需要Spring选择                                    无脑选

## 大模型选型
1.自研(算法c++,python 深度学习 机器学习 神经网络  视觉处理 985/211)
2.云端大模型
3.开源的大模型(本地部署) ollama 购买算力
    a 选型
    b 自己构建选型--> 评估流程
        i.业务确定：电商 医疗 教育
        ii. 样本准备：数据集样本 选这题
        iii.任务定制：问答
        iV. 人工评估
    c 通用能力比较好的
    