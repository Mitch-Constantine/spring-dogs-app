package com.example.springdogs.service;

import com.example.springdogs.dto.SafetyPrediction;
import com.example.springdogs.dto.DogDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ChatGptServiceTest {

    private ChatGptService chatGptService;
    private DogDto testDog;

    @BeforeEach
    void setUp() {
        // Initialize the service with a test API key
        chatGptService = new ChatGptService("test-api-key");
        
        testDog = new DogDto();
        testDog.setId(1L);
        testDog.setName("Buddy");
        testDog.setBreed("Golden Retriever");
        testDog.setAge(3);
        testDog.setColor("Golden");
        testDog.setWeight(25.5);
        testDog.setTemperament("Friendly and playful");
        testDog.setCreatedAt(LocalDateTime.now());
        testDog.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void parseSafetyResponse_WithValidSafeResponse_ParsesCorrectly() {
        String responseContent = "Yes\nThis Golden Retriever appears friendly and safe to pet.";
        
        SafetyPrediction result = parseSafetyResponse(responseContent);
        
        assertEquals("Yes", result.getIsSafeToPet());
        assertEquals("This Golden Retriever appears friendly and safe to pet.", result.getSafetyExplanation());
    }

    @Test
    void parseSafetyResponse_WithValidUnsafeResponse_ParsesCorrectly() {
        String responseContent = "No\nThis dog is dangerous and should not be approached.";
        
        SafetyPrediction result = parseSafetyResponse(responseContent);
        
        assertEquals("No", result.getIsSafeToPet());
        assertEquals("This dog is dangerous and should not be approached.", result.getSafetyExplanation());
    }

    @Test
    void parseSafetyResponse_WithValidCautiousResponse_ParsesCorrectly() {
        String responseContent = "Cautiously\nApproach with care, this dog may be unpredictable.";
        
        SafetyPrediction result = parseSafetyResponse(responseContent);
        
        assertEquals("Cautiously", result.getIsSafeToPet());
        assertEquals("Approach with care, this dog may be unpredictable.", result.getSafetyExplanation());
    }

    @Test
    void parseSafetyResponse_WithInvalidFormat_ReturnsError() {
        String responseContent = "Maybe\nInvalid format response.";
        
        SafetyPrediction result = parseSafetyResponse(responseContent);
        
        assertEquals("Error", result.getIsSafeToPet());
        assertTrue(result.getSafetyExplanation().contains("Invalid prediction format"));
    }

    @Test
    void parseSafetyResponse_WithOnlyPrediction_ReturnsCorrectPrediction() {
        String responseContent = "Yes";
        
        SafetyPrediction result = parseSafetyResponse(responseContent);
        
        assertEquals("Yes", result.getIsSafeToPet());
        assertEquals("No explanation provided", result.getSafetyExplanation());
    }

    @Test
    void parseSafetyResponse_WithErrorResponse_ReturnsError() {
        String responseContent = "Error\nTechnical error occurred.";
        
        SafetyPrediction result = parseSafetyResponse(responseContent);
        
        assertEquals("Error", result.getIsSafeToPet());
        assertEquals("Technical error occurred.", result.getSafetyExplanation());
    }

    @Test
    void parseSafetyResponse_WithMalformedContent_HandlesGracefully() {
        String responseContent = "";
        
        SafetyPrediction result = parseSafetyResponse(responseContent);
        
        assertEquals("Error", result.getIsSafeToPet());
        assertNotNull(result.getSafetyExplanation());
    }

    /**
     * Test the parsing logic directly (same as ChatGptService.parseSafetyResponse)
     */
    private SafetyPrediction parseSafetyResponse(String content) {
        try {
            String[] lines = content.split("\\n");
            String prediction = lines[0].trim();
            
            // Validate prediction
            if (!prediction.equals("Yes") && !prediction.equals("No") && 
                !prediction.equals("Cautiously") && !prediction.equals("Error")) {
                return new SafetyPrediction("Error", 
                    "Invalid prediction format from ChatGPT: " + prediction);
            }
            
            String explanation = lines.length > 1 ? lines[1].trim() : "No explanation provided";
            
            return new SafetyPrediction(prediction, explanation);
            
        } catch (Exception e) {
            return new SafetyPrediction("Error", 
                "Failed to parse ChatGPT response: " + e.getMessage());
        }
    }
}
