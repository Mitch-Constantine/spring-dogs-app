package com.example.springdogs.service;

import com.example.springdogs.dto.ChatGptRequest;
import com.example.springdogs.dto.ChatGptResponse;
import com.example.springdogs.dto.ChatGptDogDto;
import com.example.springdogs.dto.SafetyPrediction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class ChatGptService {
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    
    // Simple cache to avoid duplicate API calls
    private final Map<String, SafetyPrediction> cache = new ConcurrentHashMap<>();
    
    public ChatGptService(@Value("${app.openaiApiKey}") String apiKey) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    public SafetyPrediction predictDogSafety(ChatGptDogDto dogDto) {
        try {
            String dogJson = objectMapper.writeValueAsString(dogDto);
            
            // Check cache first to avoid repeated API calls
            if (cache.containsKey(dogJson)) {
                return cache.get(dogJson);
            }
            
            String prompt = String.format(
                "Analyze this dog data for petting safety. You MUST respond with exactly one of these four words: " +
                "YES (clearly safe/friendly), NO (clearly dangerous/aggressive), CAUTIOUSLY (requires caution), ERROR (invalid data).\n\n" +
                "Rules:\n" +
                "- If dog shows obvious signs of friendliness, therapy work, gentle temperament = YES\n" + 
                "- If dog shows obvious signs of aggression, biting history, dangerous behavior = NO\n" +
                "- If uncertain or mixed signals = CAUTIOUSLY\n" +
                "- If nonsensical breed/age/weight = ERROR\n\n" +
                "Respond with the word only, then new line, then brief explanation.\n\n" +
                "Dog data:%s", dogJson
            );
            
            ChatGptRequest request = new ChatGptRequest();
            request.setModel("gpt-3.5-turbo");
            request.setMessages(List.of(new ChatGptRequest.Message("user", prompt)));
	        request.setTemperature(0.1);
            request.setMax_tokens(210);

            ChatGptResponse response = webClient.post()
                    .uri(OPENAI_API_URL)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ChatGptResponse.class)
                    .timeout(java.time.Duration.ofSeconds(30))
                    .block();

            if (response == null || response.getChoices() == null ||
                response.getChoices().length == 0) {
                return new SafetyPrediction("Error", "API call failed - no response received");
            }

            String content = response.getChoices()[0].getMessage().getContent();
            SafetyPrediction result = parseSafetyResponse(content);
            
            // Cache the result for future identical requests
            cache.put(dogJson, result);
            return result;

        } catch (org.springframework.web.reactive.function.client.WebClientResponseException.TooManyRequests e) {
            // Rate limit exceeded - return a conservative prediction
            return new SafetyPrediction("Cautiously", 
                "Rate limit exceeded. Please wait before making more requests. Prediction based on breed: " + dogDto.getBreed());
        } catch (Exception e) {
            return new SafetyPrediction("Error",
                "Technical error occurred: " + e.getMessage());
        }
    }
    
    public SafetyPrediction parseSafetyResponse(String content) {
        try {
            String[] lines = content.split("\\n");
            String predictionRaw = lines[0].trim();
            
            // Normalize prediction to proper case (ChatGPT might return YES/NO/all caps)
            String prediction = normalizePrediction(predictionRaw);
            
            // Validate prediction
            if (!prediction.equals("Yes") && !prediction.equals("No") && 
                !prediction.equals("Cautiously") && !prediction.equals("Error")) {
                return new SafetyPrediction("Error", 
                    "Invalid prediction format from ChatGPT: " + predictionRaw);
            }
            
            String explanation = lines.length > 1 ? lines[1].trim() : "No explanation provided";
            
            return new SafetyPrediction(prediction, explanation);
            
        } catch (Exception e) {
            return new SafetyPrediction("Error", 
                "Failed to parse ChatGPT response: " + e.getMessage());
        }
    }
    
    private String normalizePrediction(String rawPrediction) {
        String normalized = rawPrediction.toLowerCase().trim();
        
        // Handle various forms ChatGPT might return
        if (normalized.equals("yes") || normalized.equals("y")) {
            return "Yes";
        } else if (normalized.equals("no") || normalized.equals("n")) {
            return "No";  
        } else if (normalized.equals("cautiously") || normalized.equals("caution")) {
            return "Cautiously";
        } else if (normalized.equals("error")) {
            return "Error";
        } else {
            // Return original if no match (will be caught by validation)
            return rawPrediction;
        }
    }
}
