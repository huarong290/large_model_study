package com.ai.study.controller;

import com.ai.study.model.EvaluationRequest;
import com.ai.study.model.EvaluationResult;
import com.ai.study.service.EvaluationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/evaluation")
@Slf4j
public class EvaluationController {
    @Autowired
    private  EvaluationService evaluationService;

    // ================================================================
    // 1. 自动评分
    // ================================================================

    @PostMapping("/score")
    public Map<String, Object> autoScore(@RequestBody EvaluationRequest request) {
        log.info("📊 自动评分请求：{}", request.getQuestion());

        try {
            EvaluationResult result = evaluationService.autoEvaluate(
                    request.getQuestion(),
                    request.getAnswer(),
                    request.getDimensions()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("question", request.getQuestion());
            response.put("scores", result.getScores());
            response.put("overallScore", result.getOverallScore());
            response.put("comment", result.getComment());
            response.put("evaluationType", result.getEvaluationType());
            return response;

        } catch (Exception e) {
            log.error("❌ 自动评分失败：{}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    // ================================================================
    // 2. 模型对比
    // ================================================================

    @PostMapping("/compare")
    public Map<String, Object> compareModels(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        log.info("📊 模型对比请求：{}", question);

        try {
            Map<String, EvaluationResult> results = evaluationService.compareModels(question);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("question", question);
            response.put("results", results);

            // 找出最佳模型
            String bestModel = null;
            Double bestScore = -1.0;
            for (Map.Entry<String, EvaluationResult> entry : results.entrySet()) {
                if (entry.getValue().getOverallScore() != null &&
                        entry.getValue().getOverallScore() > bestScore) {
                    bestScore = entry.getValue().getOverallScore();
                    bestModel = entry.getKey();
                }
            }
            response.put("bestModel", bestModel);
            response.put("bestScore", bestScore);

            return response;

        } catch (Exception e) {
            log.error("❌ 模型对比失败：{}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    // ================================================================
    // 3. A/B 测试
    // ================================================================

    @PostMapping("/ab-test")
    public Map<String, Object> abTest(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        log.info("📊 A/B 测试请求：{}", question);

        try {
            var result = evaluationService.abTest(question);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("question", question);
            response.put("groupA", result.getGroupA());
            response.put("groupB", result.getGroupB());
            response.put("conclusion", result.getConclusion());
            response.put("recommendation", result.getRecommendation());
            return response;

        } catch (Exception e) {
            log.error("❌ A/B 测试失败：{}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    // ================================================================
    // 4. 人工评估
    // ================================================================

    @PostMapping("/manual")
    public Map<String, Object> manualEvaluate(@RequestBody EvaluationRequest request) {
        log.info("📝 人工评估请求：{}，评估人：{}", request.getQuestion(), request.getEvaluator());

        try {
            Map<String, Double> scores = new HashMap<>();
            if (request.getDimensions() != null) {
                for (String dim : request.getDimensions()) {
                    // 模拟人工评分（实际应由前端传入）
                    scores.put(dim, 7.0 + Math.random() * 3.0);
                }
            }

            EvaluationResult result = evaluationService.manualEvaluate(
                    request.getQuestion(),
                    request.getAnswer(),
                    scores,
                    request.getEvaluator()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("result", result);
            return response;

        } catch (Exception e) {
            log.error("❌ 人工评估失败：{}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    // ================================================================
    // 5. 统计信息
    // ================================================================

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = evaluationService.getEvaluationStats();
        stats.put("status", "UP");
        return stats;
    }
}
