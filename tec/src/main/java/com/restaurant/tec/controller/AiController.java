package com.restaurant.tec.controller;

import com.restaurant.tec.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para funcionalidades de inteligencia artificial.
 * Proporciona endpoints para análisis de alérgenos usando LLM local.
 * 
 * @author RestaurantTec Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/admin/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    /**
     * Analiza texto de ingredientes para identificar alérgenos.
     * 
     * @param payload objeto con campo "text" conteniendo la descripción de ingredientes
     * @return ResponseEntity con lista de alérgenos detectados
     */
    @PostMapping("/analyze")
    public ResponseEntity<List<String>> analyzeAllergens(@RequestBody Map<String, String> payload) {
        String text = payload.get("text");
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(aiService.analyzeAllergens(text));
    }
}
