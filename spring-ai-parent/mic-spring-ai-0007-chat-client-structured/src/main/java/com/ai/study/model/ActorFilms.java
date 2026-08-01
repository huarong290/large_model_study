package com.ai.study.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 演员及其电影作品信息模型
 *
 * <p>包含演员的基本信息以及其参演的电影列表，演示了嵌套对象的映射。</p>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "演员及其电影作品信息模型")
public class ActorFilms implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 演员姓名
     */
    @Schema(description = "演员姓名", example = "梁朝伟")
    private String actor;

    /**
     * 演员年龄
     */
    @Schema(description = "演员年龄", example = "60")
    private Integer age;

    /**
     * 国籍
     */
    @Schema(description = "国籍", example = "中国")
    private String nationality;

    /**
     * 参演电影列表
     */
    @Schema(description = "参演电影列表")
    private List<Film> films;

    /**
     * 电影信息内部类
     *
     * @author AI Study
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "电影信息")
    public static class Film implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 电影名称
         */
        @Schema(description = "电影名称", example = "花样年华")
        private String title;

        /**
         * 上映年份
         */
        @Schema(description = "上映年份", example = "2000")
        private Integer year;

        /**
         * 演员在片中饰演的角色
         */
        @Schema(description = "饰演角色", example = "周慕云")
        private String role;

        /**
         * 电影评分（1-10分）
         */
        @Schema(description = "电影评分（1-10分）", example = "8.5", minimum = "1", maximum = "10")
        private Double rating;

        /**
         * 导演姓名
         */
        @Schema(description = "导演姓名", example = "王家卫")
        private String director;

        /**
         * 合作演员列表
         */
        @Schema(description = "合作演员列表", example = "[\"张曼玉\", \"刘德华\"]")
        private List<String> coActors;
    }
}