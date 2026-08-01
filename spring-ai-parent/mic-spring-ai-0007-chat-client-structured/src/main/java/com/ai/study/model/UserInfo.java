package com.ai.study.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 用户信息模型
 *
 * <p>用于接收 AI 生成的结构化用户数据，支持将 JSON 响应自动映射为 Java 对象。</p>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户信息模型")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户姓名
     */
    @Schema(description = "用户姓名", example = "张三")
    private String name;

    /**
     * 用户年龄
     */
    @Schema(description = "用户年龄", example = "25")
    private Integer age;

    /**
     * 用户性别（男/女/其他）
     */
    @Schema(description = "用户性别", example = "男", allowableValues = {"男", "女", "其他"})
    private String gender;

    /**
     * 所在城市
     */
    @Schema(description = "所在城市", example = "北京")
    private String city;

    /**
     * 职业/职位
     */
    @Schema(description = "职业", example = "软件工程师")
    private String occupation;

    /**
     * 兴趣爱好列表
     */
    @Schema(description = "兴趣爱好列表", example = "[\"编程\", \"阅读\", \"篮球\"]")
    private List<String> hobbies;

    /**
     * 电子邮箱地址
     */
    @Schema(description = "电子邮箱", example = "zhangsan@example.com")
    private String email;

    /**
     * 手机号码
     */
    @Schema(description = "手机号码", example = "13800138000")
    private String phone;

    /**
     * 个人简介/描述
     */
    @Schema(description = "个人简介", example = "热爱技术的软件工程师，喜欢探索新技术")
    private String description;
}