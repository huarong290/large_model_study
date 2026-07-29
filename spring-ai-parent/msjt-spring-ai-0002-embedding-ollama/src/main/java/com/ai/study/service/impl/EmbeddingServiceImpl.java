package com.ai.study.service.impl;

import com.ai.study.service.EmbeddingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class EmbeddingServiceImpl implements EmbeddingService {

    private EmbeddingModel embeddingModel;

    private final   List<float []> docVectors ;
    // 准备文本集合
   private final List<String> docs = List.of("美食非常美味，服务员也很友好",
            "这部电影既刺激又令人兴奋",
            "阅读书籍是扩展知识的好方法");

    EmbeddingServiceImpl(EmbeddingModel embeddingModel){
        this.embeddingModel = embeddingModel;
        // 在构造函数中对知识库文本进行向量化初始化向量
        this.docVectors = embeddingModel.embed(docs);
    }
    @Override
    public String queryBastMatch(String query) {
        log.info("查询: {}", query);
        // 1.对用户传入的query 进行向量化
       float[] queryVec = embeddingModel.embed(query);
        log.info("查询向量维度: {}", queryVec.length);
       // 记录目前最大的相似度
        double bestSim = -1;
        // 记录与当前输入文本最相似文本的下标
        int bastIdx = 0;
        // 2.遍历docVertors 来与用护传入的文本向量进行计算相似度，找出最相似一个返回
        for (int i = 0; i <docVectors.size() ; i++) {
            // 计算余弦相似度
          double sim =  cosineSimilarity(queryVec,docVectors.get(i));
            log.info("  文档 {} 相似度: {}", i, sim);
          if(sim>bestSim){
              bestSim = sim;
              bastIdx = i;
          }

        }
        //两个向量余弦相似度计算
        String result = docs.get(bastIdx);
        log.info("✅ 最相似文档: {} (相似度: {})", result, bestSim);
        return result;
    }

    /**
     *
     * @param a
     * @param b
     * @return
     */
    private double cosineSimilarity(float[] a , float [] b){
        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < a.length; i++) {
            // 点积
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        if (normA == 0 || normB == 0) {
            return 0.0;
        }
        return dot/(Math.sqrt(normA) * Math.sqrt(normB));
    }
}
