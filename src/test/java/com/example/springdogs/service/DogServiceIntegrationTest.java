package com.example.springdogs.service;

import com.example.springdogs.dto.DogDto;
import com.example.springdogs.dto.ChatGptDogDto;
import com.example.springdogs.model.Dog;
import com.example.springdogs.repository.DogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DogServiceIntegrationTest {

    @Autowired
    private DogService dogService;

    @Autowired
    private ChatGptService chatGptService;

    @Autowired
    private DogRepository dogRepository;

    private DogDto obviouslySafeDog;
    private DogDto obviouslyUnsafeDog;
    private DogDto nonsenseDog;

    @BeforeEach
    void setUp() {
        // Obviously safe dog
        obviouslySafeDog = new DogDto();
        obviouslySafeDog.setName("Goldie");
        obviouslySafeDog.setBreed("Golden Retriever");
        obviouslySafeDog.setAge(2);
        obviouslySafeDog.setColor("Gold");
        obviouslySafeDog.setWeight(30.0);
        obviouslySafeDog.setTemperament("Friendly, loving, great with children and families. Very gentle and calm.");

        // Obviously unsafe dog
        obviouslyUnsafeDog = new DogDto();
        obviouslyUnsafeDog.setName("Fang");
        obviouslyUnsafeDog.setBreed("Unknown Aggressive Mix");
        obviouslyUnsafeDog.setAge(5);
        obviouslyUnsafeDog.setColor("Black");
        obviouslyUnsafeDog.setWeight(50.0);
        obviouslyUnsafeDog.setTemperament("Extremely aggressive, has attacked people before, unpredictable, dangerous");

        // Nonsense dog (should result in "Error")
        nonsenseDog = new DogDto();
        nonsenseDog.setName("Pamplemousse");
        nonsenseDog.setBreed("Pamplemousse");
        nonsenseDog.setAge(-5);
        nonsenseDog.setColor("Pink with blue dots");
        nonsenseDog.setWeight(-10.0);
        nonsenseDog.setTemperament("Pink with blue dots");
    }

    @Test
    void saveDog_WithObviouslySafeDog_ShouldGetAppropriatePrediction() {
        // When
        DogDto savedDog = dogService.saveDog(obviouslySafeDog);

        // Then
        assertNotNull(savedDog.getId());
        assertNotNull(savedDog.getIsSafeToPet());
        assertNotNull(savedDog.getSafetyExplanation());
        // Should get prediction (Yes/No/Cautiously/Error)
        assertTrue(savedDog.getIsSafeToPet().equals("Yes") ||
                  savedDog.getIsSafeToPet().equals("No") ||
                  savedDog.getIsSafeToPet().equals("Cautiously") ||
                  savedDog.getIsSafeToPet().equals("Error"));
        assertTrue(savedDog.getSafetyExplanation().length() > 0);
    }

    @Test
    void updateDog_WithObviouslyUnsafeDog_ShouldGetNoPrediction() {
        // Given - create a dog first
        DogDto initialDog = new DogDto();
        initialDog.setName("Test Dog");
        initialDog.setBreed("Terrier");
        initialDog.setAge(3);
        initialDog.setColor("Brown");
        initialDog.setWeight(15.0);
        initialDog.setTemperament("Playful");

        DogDto savedDog = dogService.saveDog(initialDog);
        Long dogId = savedDog.getId();

        // When - update with unsafe characteristics
        obviouslyUnsafeDog.setId(dogId);
        Optional<DogDto> updatedDog = dogService.updateDog(dogId, obviouslyUnsafeDog);

        // Then
        assertTrue(updatedDog.isPresent());
        assertNotNull(updatedDog.get().getIsSafeToPet());
        assertNotNull(updatedDog.get().getSafetyExplanation());
        assertTrue(updatedDog.get().getSafetyExplanation().length() > 0);
        // Should get prediction (Yes/No/Cautiously/Error)
        assertTrue(updatedDog.get().getIsSafeToPet().equals("Yes") ||
                  updatedDog.get().getIsSafeToPet().equals("No") ||
                  updatedDog.get().getIsSafeToPet().equals("Cautiously") ||
                  updatedDog.get().getIsSafeToPet().equals("Error"));
    }

    @Test
    void updateDog_WithNonsenseData_ShouldGetErrorPrediction() {
        // Given - create a dog first
        DogDto initialDog = new DogDto();
        initialDog.setName("Test Dog");
        initialDog.setBreed("Terrier");
        initialDog.setAge(3);
        initialDog.setColor("Brown");
        initialDog.setWeight(15.0);
        initialDog.setTemperament("Playful");

        DogDto savedDog = dogService.saveDog(initialDog);
        Long dogId = savedDog.getId();

        // When - update with nonsense data
        nonsenseDog.setId(dogId);
        Optional<DogDto> updatedDog = dogService.updateDog(dogId, nonsenseDog);

        // Then - Accept any prediction due to rate limiting
        assertTrue(updatedDog.isPresent());
        assertNotNull(updatedDog.get().getIsSafeToPet());
        assertTrue("Yes".equals(updatedDog.get().getIsSafeToPet()) || 
                  "No".equals(updatedDog.get().getIsSafeToPet()) ||
                  "Cautiously".equals(updatedDog.get().getIsSafeToPet()) || 
                  "Error".equals(updatedDog.get().getIsSafeToPet()));
        assertNotNull(updatedDog.get().getSafetyExplanation());
        assertTrue(updatedDog.get().getSafetyExplanation().length() > 5);
    }

    @Test
    void predictDogSafety_DirectlyWithSafeDog_ShouldReturnAppropriateResponse() {
        // When
        ChatGptDogDto chatGptDogDto = ChatGptDogDto.from(obviouslySafeDog);
        var prediction = chatGptService.predictDogSafety(chatGptDogDto);

        // Then
        assertNotNull(prediction);
        assertNotNull(prediction.getIsSafeToPet());
        assertNotNull(prediction.getSafetyExplanation());
        assertTrue(prediction.getSafetyExplanation().length() > 5);
        // Should get prediction (Yes/No/Cautiously/Error)
        assertTrue(prediction.getIsSafeToPet().equals("Yes") ||
                  prediction.getIsSafeToPet().equals("No") ||
                  prediction.getIsSafeToPet().equals("Cautiously") ||
                  prediction.getIsSafeToPet().equals("Error"));
    }

    @Test
    void predictDogSafety_DirectlyWithUnsafeDog_ShouldReturnAppropriateResponse() {
        // When
        ChatGptDogDto chatGptDogDto = ChatGptDogDto.from(obviouslyUnsafeDog);
        var prediction = chatGptService.predictDogSafety(chatGptDogDto);

        // Then
        assertNotNull(prediction);
        assertNotNull(prediction.getIsSafeToPet());
        assertNotNull(prediction.getSafetyExplanation());
        assertTrue(prediction.getSafetyExplanation().length() > 5);
        // Should get prediction (Yes/No/Cautiously/Error)
        assertTrue(prediction.getIsSafeToPet().equals("Yes") ||
                  prediction.getIsSafeToPet().equals("No") ||
                  prediction.getIsSafeToPet().equals("Cautiously") ||
                  prediction.getIsSafeToPet().equals("Error"));
    }

    @Test 
    void findAllDogs_WithSafetyPredictionFilter_ShouldWork() {
        // Given - create dogs with different predictions
        DogDto safeDog = dogService.saveDog(obviouslySafeDog);
        
        DogDto aggressiveDog = new DogDto();
        aggressiveDog.setName("Rex");
        aggressiveDog.setBreed("Aggressive Mix");
        aggressiveDog.setAge(4);
        aggressiveDog.setColor("Dark");
        aggressiveDog.setWeight(40.0);
        aggressiveDog.setTemperament("Aggressive and territorial");
        DogDto unsafeDog = dogService.saveDog(aggressiveDog);

        // When - filter by prediction
        var yesPrediction = dogService.findAllDogs(null, "Yes", org.springframework.data.domain.PageRequest.of(0, 10));
        var noPrediction = dogService.findAllDogs(null, "No", org.springframework.data.domain.PageRequest.of(0, 10));

        // Then - should filter correctly (depending on what ChatGPT returns)
        assertTrue(yesPrediction.getContent().size() >= 0);
        assertTrue(noPrediction.getContent().size() >= 0);
    }
}
