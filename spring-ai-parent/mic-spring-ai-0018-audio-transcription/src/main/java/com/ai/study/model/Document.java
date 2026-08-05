package com.ai.study.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文档模型（用于 RAG 知识库）
 *
 * <p>表示知识库中的文档实体，用于 RAG 检索增强生成。</p>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文档模型（RAG 知识库）")
public class Document {

    /**
     * 文档唯一标识
     */
    @Schema(description = "文档唯一标识",
            example = "doc-001")
    private String id;

    /**
     * 文档内容
     *
     * <p>分块后的文本内容，用于检索和增强生成。</p>
     */
    @Schema(description = "文档内容",
            example = "Spring AI 是一个用于构建 AI 应用程序的框架...")
    private String content;

    /**
     * 文档来源
     *
     * <p>文件路径、URL 或用户输入标识。</p>
     */
    @Schema(description = "文档来源",
            example = "/data/knowledge.txt")
    private String source;

    /**
     * 文档标题
     */
    @Schema(description = "文档标题",
            example = "Spring AI 快速入门")
    private String title;

    /**
     * 关键词列表
     *
     * <p>用于检索增强，提高搜索准确性。</p>
     */
    @Schema(description = "关键词列表",
            example = "[\"Spring AI\", \"RAG\", \"向量数据库\"]")
    private List<String> keywords;

    /**
     * 文档分类
     */
    @Schema(description = "文档分类",
            example = "技术文档")
    private String category;

    /**
     * 文档块索引
     *
     * <p>标识分块后的顺序。</p>
     */
    @Schema(description = "文档块索引",
            example = "1")
    private Integer chunkIndex;

    /**
     * 文档块总数
     */
    @Schema(description = "文档块总数",
            example = "5")
    private Integer totalChunks;

    /**
     * 创建时间戳
     */
    @Schema(description = "创建时间戳",
            example = "1700000000000")
    private Long createTime;
}