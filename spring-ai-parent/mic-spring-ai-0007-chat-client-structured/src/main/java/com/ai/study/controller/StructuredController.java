package com.ai.study.controller;


import com.ai.study.model.*;
import com.ai.study.service.StructuredService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/structured")
@Slf4j
@RequiredArgsConstructor
public class StructuredController {

    private final StructuredService structuredService;

    // ================================================================
    // 1. 基础 Java Bean
    // ================================================================

    @GetMapping("/user")
    public Map<String, Object> getUserInfo(
            @RequestParam(value = "name", defaultValue = "张三") String name,
            @RequestParam(value = "city", defaultValue = "北京") String city) {

        log.info("📝 获取用户信息：{}, {}", name, city);

        try {
            UserInfo user = structuredService.getUserInfo(name, city);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", user);
            result.put("type", "Java Bean");
            return result;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    @GetMapping("/country")
    public Map<String, Object> getCountryInfo(
            @RequestParam(value = "name", defaultValue = "中国") String name) {

        log.info("📝 获取国家信息：{}", name);

        try {
            CountryInfo country = structuredService.getCountryInfo(name);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", country);
            result.put("type", "Java Bean");
            return result;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    // ================================================================
    // 2. Record
    // ================================================================

    @GetMapping("/person")
    public Map<String, Object> getPersonRecord(
            @RequestParam(value = "name", defaultValue = "李四") String name) {

        log.info("📝 获取人物信息（Record）：{}", name);

        try {
          PersonRecord person = structuredService.getPersonRecord(name);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", person);
            result.put("type", "Record");
            return result;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    // ================================================================
    // 3. List
    // ================================================================

    @GetMapping("/cities")
    public Map<String, Object> getCityList() {
        log.info("📝 获取城市列表");

        try {
            List<String> cities = structuredService.getCityList();

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", cities);
            result.put("type", "List<String>");
            result.put("count", cities.size());
            return result;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    @GetMapping("/famous-people")
    public Map<String, Object> getFamousPeople() {
        log.info("📝 获取名人列表");

        try {
            List<PersonRecord> people = structuredService.getFamousPeople();

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", people);
            result.put("type", "List<Record>");
            result.put("count", people.size());
            return result;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    // ================================================================
    // 4. 嵌套对象
    // ================================================================

    @GetMapping("/actor")
    public Map<String, Object> getActorFilms(
            @RequestParam(value = "name", defaultValue = "梁朝伟") String name) {

        log.info("📝 获取演员电影信息：{}", name);

        try {
            ActorFilms actor = structuredService.getActorFilms(name);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", actor);
            result.put("type", "Nested Object");
            return result;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    // ================================================================
    // 5. 复杂对象
    // ================================================================

    @GetMapping("/recipe")
    public Map<String, Object> getRecipe(
            @RequestParam(value = "name", defaultValue = "宫保鸡丁") String name) {

        log.info("📝 获取菜谱：{}", name);

        try {
            Recipe recipe = structuredService.getRecipe(name);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", recipe);
            result.put("type", "Complex Object");
            return result;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    // ================================================================
    // 6. 批量处理
    // ================================================================

    @GetMapping("/countries")
    public Map<String, Object> getMultipleCountries(
            @RequestParam(value = "names", defaultValue = "中国,日本,韩国") String names) {

        log.info("📝 批量获取国家信息：{}", names);

        try {
            List<String> countryList = List.of(names.split(","));
            List<CountryInfo> countries = structuredService.getMultipleCountries(countryList);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", countries);
            result.put("type", "List<CountryInfo>");
            result.put("count", countries.size());
            return result;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    // ================================================================
    // 7. 对比展示
    // ================================================================

    @GetMapping("/compare")
    public Map<String, Object> compareMethods(
            @RequestParam(value = "name", defaultValue = "张三") String name) {

        log.info("📝 对比不同输出方式");

        Map<String, Object> result = new HashMap<>();

        try {
            // 1. Java Bean
            UserInfo user = structuredService.getUserInfo(name, "北京");
            result.put("javaBean", user);

            // 2. Record
            PersonRecord person = structuredService.getPersonRecord(name);
            result.put("record", person);

            // 3. List
            List<String> cities = structuredService.getCityList();
            result.put("list", cities.subList(0, Math.min(5, cities.size())));

            result.put("success", true);
            result.put("summary", "三种方式对比：Java Bean、Record、List");

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }
}
