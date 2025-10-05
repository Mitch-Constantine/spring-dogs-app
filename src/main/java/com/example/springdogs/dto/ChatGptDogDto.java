package com.example.springdogs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatGptDogDto {
    private String name;
    private String breed;
    private Integer age;
    private String color;
    private Double weight;
    private String temperament;
    
    // Static method to create from DogDto
    public static ChatGptDogDto from(DogDto dogDto) {
        ChatGptDogDto dto = new ChatGptDogDto();
        dto.setName(dogDto.getName());
        dto.setBreed(dogDto.getBreed());
        dto.setAge(dogDto.getAge());
        dto.setColor(dogDto.getColor());
        dto.setWeight(dogDto.getWeight());
        dto.setTemperament(dogDto.getTemperament());
        return dto;
    }
}
