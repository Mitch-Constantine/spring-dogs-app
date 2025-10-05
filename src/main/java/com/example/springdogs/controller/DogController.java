package com.example.springdogs.controller;

import com.example.springdogs.dto.DogDto;
import com.example.springdogs.model.Dog;
import com.example.springdogs.repository.DogRepository;
import com.example.springdogs.service.DogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/dogs")
@CrossOrigin(origins = "http://localhost:3000")
public class DogController {

    @Autowired
    private DogService dogService;

    @GetMapping
    public ResponseEntity<Page<DogDto>> getAllDogs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String prediction,
            Pageable pageable) {
        Page<DogDto> dogs = dogService.findAllDogs(search, prediction, pageable);
        return ResponseEntity.ok(dogs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DogDto> getDogById(@PathVariable Long id) {
        Optional<DogDto> dog = dogService.findDogById(id);
        return dog.map(ResponseEntity::ok)
                 .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DogDto> createDog(@Valid @RequestBody DogDto dogDto) {
        DogDto createdDog = dogService.saveDog(dogDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDog);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DogDto> updateDog(@PathVariable Long id, @Valid @RequestBody DogDto dogDto) {
        Optional<DogDto> updatedDog = dogService.updateDog(id, dogDto);
        return updatedDog.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDog(@PathVariable Long id) {
        if (dogService.deleteDog(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<java.util.Map<String, Long>> getDogStats() {
        return ResponseEntity.ok(dogService.getDogStats());
    }
}

