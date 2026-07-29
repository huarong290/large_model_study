package com.ai.study.service.impl;

import com.ai.study.service.RagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RagServiceImpl implements RagService {

    //存储本地文档切分内容
    private List<String> docs = new ArrayList<>();
    // 存储本地文档对应的向量
    private final   List<float []> docVectors = new ArrayList<>();

    private EmbeddingModel embeddingModel;

    //聊天客户端
    private ChatClient chatClient;

    public RagServiceImpl(EmbeddingModel embeddingModel,ChatClient.Builder chatBuilder ) throws IOException {

        this.embeddingModel = embeddingModel;
        this.chatClient = chatBuilder.build();
        //1.加载本地文档
       ClassPathResource classPathResource = new ClassPathResource("中国古代诗歌常用意象大全.txt");
       String content = new String(classPathResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

       //2.分割文档内容 通过embedding 生成向量
        String[]  strContents =content.split("----");
        for (String part: strContents) {

            log.info("part=======>{}",part);
            //存储切分的内容
            docs.add(part);
            // 将文档片段进行向量化存储
            this.docVectors.add(this.embeddingModel.embed(part));
        }
    }


    @Override
    public String answer(String question) {
        // 对用户提问进行内容向量化
        float[] qv = embeddingModel.embed(question);

        // 定义 相似度最大的top2
        double v1 = -1;
        double v2 = -1;
        // 定义 相似度最大的top2 文档索引
        int v1_index = -1;
        int v2_index = -1;
        // 将向量化内容与知识库中各个向量进行相似度对比，获取最相似的top2
        for (int i = 0; i < docVectors.size(); i++) {
          double sim =  cosineSimilarity(qv,docVectors.get(i));
            if(sim > v1) {
                // 将原来的 top1 降为 top2
                v2 = v1 ;
                v2_index = v1_index;

                v1 = sim;
                v1_index = i;
            }else if(sim > v2){
                v2 = sim;
                v2_index = i;
            }
        }
        log.info("Top1: {}, 相似度: {}", v1_index, v1);
        log.info("Top2: {}, 相似度: {}", v2_index, v2);
        // 获取top2 最相似内容 拼接在一起作为上下文/prompt提供给LLM
        String ctx = "";
        if(v1_index !=-1){
          String doc2=  v2_index >=0 ? "\n----\n"+docs.get(v2_index) :"\n----\n";
            ctx = docs.get(v1_index)+doc2;
        }
        //
        String prompt = "以下是知识库内容："+ctx+"\n请给予上述知识库内容回答用户问题："+question;
        ChatClient.CallResponseSpec responseSpec =chatClient.prompt().system("你是知识助手，结合上下文回答用户问题").user(prompt).call();
        return responseSpec.content();
    }

    /**
     * 余弦相似度
     * @param a 参数a
     * @param b 参数b
     * @return 返回结果
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
