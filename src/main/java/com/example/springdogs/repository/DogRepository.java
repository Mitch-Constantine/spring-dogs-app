package com.example.springdogs.repository;

import com.example.springdogs.model.Dog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DogRepository extends JpaRepository<Dog, Long> {
    
    // Search dogs by various criteria
    Page<Dog> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    Page<Dog> findByBreedContainingIgnoreCase(String breed, Pageable pageable);
    
    Page<Dog> findByOwnerNameContainingIgnoreCase(String ownerName, Pageable pageable);
    
    Page<Dog> findByStatus(Dog.DogStatus status, Pageable pageable);
    
    // Combined search
    @Query("SELECT d FROM Dog d WHERE " +
           "(LOWER(d.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
           "(LOWER(d.breed) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
           "(LOWER(d.ownerName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Dog> searchDogs(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Count dogs by status
    Long countByStatus(Dog.DogStatus status);
}

