package com.example.springdogs.service;

import com.example.springdogs.dto.DogDto;
import com.example.springdogs.model.Dog;
import com.example.springdogs.repository.DogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class DogService {

    @Autowired
    private DogRepository dogRepository;

    public Page<DogDto> findAllDogs(String search, Pageable pageable) {
        Page<Dog> dogs;
        if (search != null && !search.trim().isEmpty()) {
            dogs = dogRepository.searchDogs(search, pageable);
        } else {
            dogs = dogRepository.findAll(pageable);
        }
        return dogs.map(DogDto::fromEntity);
    }

    public Optional<DogDto> findDogById(Long id) {
        return dogRepository.findById(id)
                           .map(DogDto::fromEntity);
    }

    public DogDto saveDog(DogDto dogDto) {
        Dog dog = dogDto.toEntity();
        Dog savedDog = dogRepository.save(dog);
        return DogDto.fromEntity(savedDog);
    }

    public Optional<DogDto> updateDog(Long id, DogDto dogDto) {
        if (!dogRepository.existsById(id)) {
            return Optional.empty();
        }
        
        Dog existingDog = dogRepository.findById(id).orElseThrow();
        existingDog.setName(dogDto.getName());
        existingDog.setBreed(dogDto.getBreed());
        existingDog.setAge(dogDto.getAge());
        existingDog.setColor(dogDto.getColor());
        existingDog.setWeight(dogDto.getWeight());
        existingDog.setOwnerName(dogDto.getOwnerName());
        existingDog.setOwnerPhone(dogDto.getOwnerPhone());
        existingDog.setOwnerEmail(dogDto.getOwnerEmail());
        existingDog.setBirthDate(dogDto.getBirthDate());
        existingDog.setMedicalNotes(dogDto.getMedicalNotes());
        existingDog.setStatus(dogDto.getStatus());
        
        Dog updatedDog = dogRepository.save(existingDog);
        return Optional.of(DogDto.fromEntity(updatedDog));
    }

    public boolean deleteDog(Long id) {
        if (!dogRepository.existsById(id)) {
            return false;
        }
        dogRepository.deleteById(id);
        return true;
    }

    public Map<String, Long> getDogStats() {
        Map<String, Long> stats = new HashMap<>();
        for (Dog.DogStatus status : Dog.DogStatus.values()) {
            stats.put(status.name(), dogRepository.countByStatus(status));
        }
        stats.put("TOTAL", dogRepository.count());
        return stats;
    }
}

