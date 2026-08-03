package com.ai.study.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 文档模型
 *
 * <p>用于表示知识库中的文档实体，包含文档内容、元数据等信息。</p>
 * <p>文档会被分块存储，每个块作为一个独立的 Document 实体。</p>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文档模型")
public class Document implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文档唯一标识（UUID）
     */
    @Schema(description = "文档唯一标识", example = "550e8400-e29b-41d4-a716-446655440000")
    private String id;

    /**
     * 文档内容（分块后的文本）
     */
    @Schema(description = "文档内容", example = "Spring AI 是一个用于构建 AI 应用程序的框架...")
    private String content;

    /**
     * 文档来源（文件路径、URL 或用户输入）
     */
    @Schema(description = "文档来源", example = "/data/knowledge.txt")
    private String source;

    /**
     * 文档标题
     */
    @Schema(description = "文档标题", example = "Spring AI 快速入门")
    private String title;

    /**
     * 关键词列表（用于检索增强）
     */
    @Schema(description = "关键词列表", example = "[\"Spring AI\", \"RAG\", \"向量数据库\"]")
    private List<String> keywords;

    /**
     * 文档分类
     */
    @Schema(description = "文档分类", example = "技术文档")
    private String category;

    /**
     * 文档创建时间戳
     */
    @Schema(description = "创建时间戳", example = "1700000000000")
    private Long createTime;

    /**
     * 文档块索引（用于标识分块顺序）
     */
    @Schema(description = "文档块索引", example = "1")
    private Integer chunkIndex;

    /**
     * 文档块总数
     */
    @Schema(description = "文档块总数", example = "5")
    private Integer totalChunks;
}