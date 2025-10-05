package com.example.springdogs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SafetyPrediction {
    private String isSafeToPet; // "Yes", "No", "Cautiously", "Error"
    private String safetyExplanation;
}
