package com.ai.study.service;

import com.ai.study.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class StructuredService {

    @Qualifier("ollamaChatClient")
    @Autowired
    private  ChatClient ollamaChatClient;

    /**
     * 获取用户信息（映射到 UserInfo Bean）
     */
    public UserInfo getUserInfo(String name, String city) {
        log.info("📝 获取用户信息：name={}, city={}", name, city);

        String prompt = String.format("""
                请生成一个名为 %s、住在 %s 的虚拟用户信息。
                请以 JSON 格式返回，包含以下字段：
                - name: 姓名
                - age: 年龄（整数）
                - gender: 性别
                - city: 城市
                - occupation: 职业
                - hobbies: 爱好列表
                - email: 邮箱
                - phone: 电话
                - description: 个人简介
                
                只返回 JSON，不要其他内容。
                """, name, city);

        return ollamaChatClient.prompt()
                .user(prompt)
                .call()
                .entity(UserInfo.class);
    }

    /**
     * 获取国家信息
     */
    public CountryInfo getCountryInfo(String countryName) {
        log.info("📝 获取国家信息：{}", countryName);

        String prompt = String.format("""
                请提供 %s 的详细信息，以 JSON 格式返回：
                - name: 国家名称
                - capital: 首都
                - area: 面积
                - population: 人口
                - language: 官方语言
                - currency: 货币
                - timezone: 时区
                - majorCities: 主要城市列表
                - description: 国家简介
                
                只返回 JSON，不要其他内容。
                """, countryName);

        return ollamaChatClient.prompt()
                .user(prompt)
                .call()
                .entity(CountryInfo.class);
    }

    /**
     * 获取人物信息（映射到 Record）
     */
    public PersonRecord getPersonRecord(String name) {
        log.info("📝 获取人物信息（Record）：{}", name);

        String prompt = String.format("""
                请生成一个名为 %s 的虚拟人物信息。
                以 JSON 格式返回：name, age, city, occupation
                只返回 JSON，不要其他内容。
                """, name);

        return ollamaChatClient.prompt()
                .user(prompt)
                .call()
                .entity(PersonRecord.class);
    }

    /**
     * 获取城市列表
     */
    public List<String> getCityList() {
        log.info("📝 获取城市列表");

        String prompt = """
                请列出中国10个主要城市的名称。
                以 JSON 数组格式返回，只返回城市名称列表，不要其他内容。
                """;

        return ollamaChatClient.prompt()
                .user(prompt)
                .call()
                .entity(new ParameterizedTypeReference<List<String>>() {});
    }

    /**
     * 获取名人列表（映射到 PersonRecord）
     */
    public List<PersonRecord> getFamousPeople() {
        log.info("📝 获取名人列表");

        String prompt = """
                请列出5位中国著名科学家。
                以 JSON 数组格式返回，每个人包含：name, age, city, occupation
                只返回 JSON 数组，不要其他内容。
                """;

        return ollamaChatClient.prompt()
                .user(prompt)
                .call()
                .entity(new ParameterizedTypeReference<List<PersonRecord>>() {});
    }

    /**
     * 获取演员及其电影信息（嵌套对象）
     */
    public ActorFilms getActorFilms(String actorName) {
        log.info("📝 获取演员电影信息：{}", actorName);

        String prompt = String.format("""
                请提供演员 %s 的信息及其代表作品。
                以 JSON 格式返回：
                - actor: 演员姓名
                - age: 年龄
                - nationality: 国籍
                - films: 电影列表，每部电影包含：
                  - title: 片名
                  - year: 年份
                  - role: 角色
                  - rating: 评分（1-10）
                  - director: 导演
                  - coActors: 合作演员列表
                
                只返回 JSON，不要其他内容。
                """, actorName);

        return ollamaChatClient.prompt()
                .user(prompt)
                .call()
                .entity(ActorFilms.class);
    }

    /**
     * 获取菜谱（复杂对象）
     */
    public Recipe getRecipe(String dishName) {
        log.info("📝 获取菜谱：{}", dishName);

        String prompt = String.format("""
                请提供 %s 的菜谱。
                以 JSON 格式返回：
                - name: 菜名
                - cuisine: 菜系
                - prepTime: 准备时间（分钟）
                - cookTime: 烹饪时间（分钟）
                - totalTime: 总时间（分钟）
                - ingredients: 食材列表
                - steps: 步骤列表
                - tips: 小贴士列表
                - difficulty: 难度（简单/中等/困难）
                - description: 菜品简介
                
                只返回 JSON，不要其他内容。
                """, dishName);

        return ollamaChatClient.prompt()
                .user(prompt)
                .call()
                .entity(Recipe.class);
    }

    /**
     * 批量获取国家信息
     */
    public List<CountryInfo> getMultipleCountries(List<String> countries) {
        log.info("📝 批量获取国家信息：{}", countries);

        String countriesStr = String.join("、", countries);
        String prompt = String.format("""
                请提供 %s 的详细信息。
                以 JSON 数组格式返回，每个国家包含：
                name, capital, area, population, language, currency, timezone, majorCities, description
                只返回 JSON 数组，不要其他内容。
                """, countriesStr);

        return ollamaChatClient.prompt()
                .user(prompt)
                .call()
                .entity(new ParameterizedTypeReference<List<CountryInfo>>() {});
    }
}