package com.ai.study;

import com.ai.study.model.*;
import com.ai.study.service.StructuredService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

@SpringBootTest
@Slf4j
public class StructuredTest {

    @DynamicPropertySource
    static void loadEnvProperties(DynamicPropertyRegistry registry) {
        try {
            Properties props = new Properties();
            String currentDir = System.getProperty("user.dir");
            File envFile = findEnvFile(new File(currentDir));
            if (envFile != null && envFile.exists()) {
                try (FileInputStream fis = new FileInputStream(envFile)) {
                    props.load(fis);
                    log.info("✅ .env 文件加载成功");
                }
            }
            props.forEach((key, value) -> {
                String strKey = key.toString();
                String strValue = value.toString();
                if (!strKey.startsWith("#") && !strValue.isEmpty()) {
                    registry.add(strKey, () -> strValue);
                }
            });
        } catch (Exception e) {
            log.warn("加载 .env 文件失败：{}", e.getMessage());
        }
    }

    private static File findEnvFile(File startDir) {
        File current = startDir;
        while (current != null) {
            File envFile = new File(current, ".env");
            if (envFile.exists()) {
                return envFile;
            }
            current = current.getParentFile();
        }
        return null;
    }

    @Autowired
    private StructuredService structuredService;

    @Test
    void testUserInfo() {
        log.info("========== 测试 UserInfo（Java Bean）==========");
        UserInfo user = structuredService.getUserInfo("王五", "上海");
        log.info("姓名：{}", user.getName());
        log.info("年龄：{}", user.getAge());
        log.info("城市：{}", user.getCity());
        log.info("职业：{}", user.getOccupation());
        log.info("爱好：{}", user.getHobbies());
        log.info("简介：{}", user.getDescription());
    }

    @Test
    void testCountryInfo() {
        log.info("========== 测试 CountryInfo ==========");
        CountryInfo country = structuredService.getCountryInfo("日本");
        log.info("国家：{}", country.getName());
        log.info("首都：{}", country.getCapital());
        log.info("人口：{}", country.getPopulation());
        log.info("主要城市：{}", country.getMajorCities());
    }

    @Test
    void testPersonRecord() {
        log.info("========== 测试 PersonRecord ==========");
        PersonRecord person = structuredService.getPersonRecord("赵六");
        log.info("姓名：{}", person.name());
        log.info("年龄：{}", person.age());
        log.info("城市：{}", person.city());
        log.info("职业：{}", person.occupation());
    }

    @Test
    void testCityList() {
        log.info("========== 测试 List<String> ==========");
        List<String> cities = structuredService.getCityList();
        log.info("城市列表：{}", cities);
        log.info("数量：{}", cities.size());
    }

    @Test
    void testActorFilms() {
        log.info("========== 测试嵌套对象 ==========");
        ActorFilms actor = structuredService.getActorFilms("巩俐");
        log.info("演员：{}", actor.getActor());
        log.info("国籍：{}", actor.getNationality());
        log.info("电影数量：{}", actor.getFilms().size());
        actor.getFilms().forEach(film -> {
            log.info("  电影：{}（{}）- 评分：{}", film.getTitle(), film.getYear(), film.getRating());
        });
    }

    @Test
    void testRecipe() {
        log.info("========== 测试复杂对象 ==========");
        Recipe recipe = structuredService.getRecipe("麻婆豆腐");
        log.info("菜名：{}", recipe.getName());
        log.info("菜系：{}", recipe.getCuisine());
        log.info("难度：{}", recipe.getDifficulty());
        log.info("食材：{}", recipe.getIngredients());
        log.info("步骤数量：{}", recipe.getSteps().size());
        recipe.getSteps().forEach(step -> log.info("  {}", step));
    }

    @Test
    void testMultipleCountries() {
        log.info("========== 测试批量处理 ==========");
        List<CountryInfo> countries = structuredService.getMultipleCountries(List.of("中国", "日本", "韩国"));
        countries.forEach(country -> {
            log.info("国家：{}，首都：{}，人口：{}",
                    country.getName(), country.getCapital(), country.getPopulation());
        });
    }
}
