package com.example.springdogs.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dogs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Dog name is required")
    @Column(nullable = false)
    private String name;
    
    @NotBlank(message = "Breed is required")
    @Column(nullable = false)
    private String breed;
    
    @NotNull(message = "Age is required")
    @Column(nullable = false)
    private Integer age;
    
    @Column
    private String color;
    
    @Column
    private Double weight;
    
    @NotBlank(message = "Owner name is required")
    @Column(name = "owner_name", nullable = false)
    private String ownerName;
    
    @Column(name = "owner_phone")
    private String ownerPhone;
    
    @Column(name = "owner_email")
    private String ownerEmail;
    
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    @Column(name = "medical_notes", length = 1000)
    private String medicalNotes;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DogStatus status = DogStatus.Active;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum DogStatus {
        Active, Inactive, Adopted, Deceased
    }
}

