package com.restaurant.tec.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Servicio de inteligencia artificial para analizar alérgenos.
 * Utiliza Ollama (LLM local) para identificar alérgenos en descripciones de platos.
 * Proporciona análisis automático de ingredientes y alertas de alergias.
 * 
 * @author RestaurantTec Team
 * @version 1.0
 */
@Service
public class AiService {

    @Value("${ai.ollama.url:http://localhost:11434/api/generate}")
    private String ollamaUrl;

    @Value("${ai.ollama.model:llama3.1:8b}")
    private String ollamaModel;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Constructor del servicio de IA.
     * Configura RestTemplate con timeouts para conexión con Ollama.
     */
    public AiService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000); // 10 seconds connection
        factory.setReadTimeout(60000); // 1 minute read timeout (GPU inference is fast)
        this.restTemplate = new RestTemplate(factory);
    }

    /**
     * Warmup: Preload Gemma-2B model into memory on startup to avoid cold start
     * delays.
     * Runs asynchronously to not block application startup.
     */
    @PostConstruct
    public void warmupModel() {
        new Thread(() -> {
            try {
                System.out.println("🔥 AI Service: Warming up " + ollamaModel + " model...");
                long startTime = System.currentTimeMillis();

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("model", ollamaModel);
                requestBody.put("prompt", "Hola");
                requestBody.put("stream", false);

                Map<String, Object> options = new HashMap<>();
                options.put("num_predict", 1); // Solo 1 token para cargar el modelo rápido
                requestBody.put("options", options);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

                restTemplate.postForEntity(ollamaUrl, request, String.class);

                long duration = System.currentTimeMillis() - startTime;
                System.out.println("✅ AI Service: Model warmed up in " + duration + "ms. Ready for fast inference!");
            } catch (Exception e) {
                System.err.println("⚠️ AI Service: Warmup failed (Ollama may be offline): " + e.getMessage());
            }
        }).start();
    }

    /**
     * Analiza un texto de ingredientes para detectar alérgenos.
     * Utiliza Ollama LLM para identificar alérgenos comunes en cada línea del texto.
     * 
     * @param text texto con descripciones de ingredientes (una por línea)
     * @return lista de alérgenos detectados
     */
    public List<String> analyzeAllergens(String text) {
        System.out.println("🤖 AI Service: Requesting analysis for text: \n" + text);

        String[] lines = text.split("\\n");

        // Parallelize analysis using CompletableFuture to avoid timeouts
        List<java.util.concurrent.CompletableFuture<List<String>>> futures = new ArrayList<>();

        for (String line : lines) {
            if (line.trim().length() < 5)
                continue;

            futures.add(java.util.concurrent.CompletableFuture.supplyAsync(() -> {
                System.out.println("🤖 AI Service: Analyzing chunk in parallel: " + line);
                return callOllama(line);
            }));
        }

        // Wait for all to finish and aggregate
        return futures.stream()
                .map(java.util.concurrent.CompletableFuture::join)
                .flatMap(List::stream)
                .distinct()
                .collect(java.util.stream.Collectors.toList());
    }

    private List<String> callOllama(String text) {
        try {
            // Prompt ultra-específico para Gemma-2B - Optimizado para clasificación de
            // alérgenos
            String prompt = "Eres un experto en seguridad alimentaria. Tu ÚNICA tarea es identificar alérgenos en ingredientes.\n\n"
                    + "REGLAS ESTRICTAS:\n"
                    + "1. SOLO responde con JSON: {\"allergens\": [...]}\n"
                    + "2. Si no hay alérgenos, devuelve: {\"allergens\": []}\n"
                    + "3. Usa EXACTAMENTE estos nombres: Gluten, Crustaceos, Huevos, Pescado, Cacahuetes, Soja, Lacteos, Frutos de cascara, Apio, Mostaza, Sesamo, Sulfitos, Altramuces, Moluscos\n\n"
                    + "MAPEO DE INGREDIENTES:\n"
                    + "- Pan, Harina, Pasta, Trigo, Cebada, Avena, Galletas, Rebozado → Gluten\n"
                    + "- Gambas, Langostinos, Cangrejos, Cigalas, Bogavante → Crustaceos\n"
                    + "- Huevo, Mayonesa, Merengue, Tortilla, Flan → Huevos\n"
                    + "- Merluza, Atún, Salmón, Bacalao, Lubina, Dorada, Anchoas, Sardinas → Pescado\n"
                    + "- Cacahuete, Maní → Cacahuetes\n"
                    + "- Soja, Tofu, Edamame, Salsa de soja → Soja\n"
                    + "- Leche, Queso, Nata, Yogur, Mantequilla, Crema → Lacteos\n"
                    + "- Nueces, Almendras, Avellanas, Pistachos, Anacardos, Piñones → Frutos de cascara\n"
                    + "- Apio → Apio\n"
                    + "- Mostaza → Mostaza\n"
                    + "- Sésamo, Aceite de sésamo, Tahini → Sesamo\n"
                    + "- Vino, Vinagre, Sidra (pueden contener) → Sulfitos\n"
                    + "- Altramuces, Lupín → Altramuces\n"
                    + "- Mejillones, Almejas, Ostras, Pulpo, Calamar, Sepia → Moluscos\n\n"
                    + "INGREDIENTES A ANALIZAR: " + text + "\n\n"
                    + "RESPUESTA JSON:";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", ollamaModel);
            requestBody.put("prompt", prompt);
            requestBody.put("stream", false);
            requestBody.put("format", "json");

            // Parámetros optimizados para clasificación determinista
            Map<String, Object> options = new HashMap<>();
            options.put("temperature", 0.0); // Determinístico para clasificación
            options.put("num_predict", 128); // Solo necesitamos la lista de alérgenos
            options.put("num_ctx", 2048); // Contexto suficiente para el mapeo
            options.put("top_p", 0.9);
            requestBody.put("options", options);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            long startTime = System.currentTimeMillis();
            ResponseEntity<String> response = restTemplate.postForEntity(ollamaUrl, request, String.class);
            long duration = System.currentTimeMillis() - startTime;

            System.out.println("🤖 AI Service: Chunk response in " + duration + "ms");

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                String responseText = root.path("response").asText();
                // System.out.println("🤖 AI Service: LLM Content: " + responseText);

                JsonNode innerJson = objectMapper.readTree(responseText);
                JsonNode allergensNode = innerJson.path("allergens");

                List<String> allergens = new ArrayList<>();
                if (allergensNode.isArray()) {
                    for (JsonNode node : allergensNode) {
                        allergens.add(node.asText());
                    }
                }
                return allergens;
            }
        } catch (Exception e) {
            System.err.println("❌ AI Service Error on chunk: " + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
