package com.example.springdogs.dto;

import com.example.springdogs.model.Dog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DogDto {
    
    private Long id;
    private String name;
    private String breed;
    private Integer age;
    private String color;
    private Double weight;
    private String ownerName;
    private String ownerPhone;
    private String ownerEmail;
    private LocalDate birthDate;
    private String medicalNotes;
    private Dog.DogStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Convert from Entity to DTO
    public static DogDto fromEntity(Dog dog) {
        DogDto dto = new DogDto();
        dto.setId(dog.getId());
        dto.setName(dog.getName());
        dto.setBreed(dog.getBreed());
        dto.setAge(dog.getAge());
        dto.setColor(dog.getColor());
        dto.setWeight(dog.getWeight());
        dto.setOwnerName(dog.getOwnerName());
        dto.setOwnerPhone(dog.getOwnerPhone());
        dto.setOwnerEmail(dog.getOwnerEmail());
        dto.setBirthDate(dog.getBirthDate());
        dto.setMedicalNotes(dog.getMedicalNotes());
        dto.setStatus(dog.getStatus());
        dto.setCreatedAt(dog.getCreatedAt());
        dto.setUpdatedAt(dog.getUpdatedAt());
        return dto;
    }
    
    // Convert from DTO to Entity
    public Dog toEntity() {
        Dog dog = new Dog();
        dog.setName(this.name);
        dog.setBreed(this.breed);
        dog.setAge(this.age);
        dog.setColor(this.color);
        dog.setWeight(this.weight);
        dog.setOwnerName(this.ownerName);
        dog.setOwnerPhone(this.ownerPhone);
        dog.setOwnerEmail(this.ownerEmail);
        dog.setBirthDate(this.birthDate);
        dog.setMedicalNotes(this.medicalNotes);
        dog.setStatus(this.status != null ? this.status : Dog.DogStatus.Active);
        return dog;
    }
}

