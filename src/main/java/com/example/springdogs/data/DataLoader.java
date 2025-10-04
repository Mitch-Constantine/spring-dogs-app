package com.example.springdogs.data;

import com.example.springdogs.model.User;
import com.example.springdogs.model.Dog;
import com.example.springdogs.repository.UserRepository;
import com.example.springdogs.repository.DogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DogRepository dogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create default admin user
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setRole(User.Role.ADMIN);
            admin.setActive(true);
            userRepository.save(admin);
        }

        // Create default guest user
        if (userRepository.findByUsername("guest").isEmpty()) {
            User guest = new User();
            guest.setUsername("guest");
            guest.setPassword(passwordEncoder.encode("guest123"));
            guest.setEmail("guest@example.com");
            guest.setFirstName("Guest");
            guest.setLastName("User");
            guest.setRole(User.Role.GUEST);
            guest.setActive(true);
            userRepository.save(guest);
        }

        // Add sample dogs for testing
        if (dogRepository.count() == 0) {
            Dog dog1 = new Dog();
            dog1.setName("Buddy");
            dog1.setBreed("Golden Retriever");
            dog1.setAge(3);
            dog1.setColor("Golden");
            dog1.setWeight(65.5);
            dog1.setOwnerName("John Smith");
            dog1.setOwnerPhone("555-0123");
            dog1.setOwnerEmail("john.smith@email.com");
            dog1.setBirthDate(LocalDate.now().minusYears(3));
            dog1.setStatus(Dog.DogStatus.Active);
            dogRepository.save(dog1);

            Dog dog2 = new Dog();
            dog2.setName("Luna");
            dog2.setBreed("German Shepherd");
            dog2.setAge(5);
            dog2.setColor("Black & Tan");
            dog2.setWeight(75.2);
            dog2.setOwnerName("Sarah Johnson");
            dog2.setOwnerPhone("555-0456");
            dog2.setOwnerEmail("sarah.j@email.com");
            dog2.setBirthDate(LocalDate.now().minusYears(5));
            dog2.setStatus(Dog.DogStatus.Active);
            dogRepository.save(dog2);

            Dog dog3 = new Dog();
            dog3.setName("Max");
            dog3.setBreed("Labrador");
            dog3.setAge(7);
            dog3.setColor("Chocolate");
            dog3.setWeight(60.8);
            dog3.setOwnerName("Mike Brown");
            dog3.setOwnerPhone("555-0789");
            dog3.setOwnerEmail("mike.brown@email.com");
            dog3.setBirthDate(LocalDate.now().minusYears(7));
            dog3.setStatus(Dog.DogStatus.Adopted);
            dogRepository.save(dog3);

            Dog dog4 = new Dog();
            dog4.setName("Bella");
            dog4.setBreed("Beagle");
            dog4.setAge(2);
            dog4.setColor("Tri-color");
            dog4.setWeight(25.3);
            dog4.setOwnerName("Emma Davis");
            dog4.setOwnerPhone("555-0321");
            dog4.setOwnerEmail("emma.davis@email.com");
            dog4.setBirthDate(LocalDate.now().minusYears(2));
            dog4.setStatus(Dog.DogStatus.Active);
            dogRepository.save(dog4);

            Dog dog5 = new Dog();
            dog5.setName("Rocky");
            dog5.setBreed("Boxer");
            dog5.setAge(4);
            dog5.setColor("Brindle");
            dog5.setWeight(55.7);
            dog5.setOwnerName("Alex Wilson");
            dog5.setOwnerPhone("555-0654");
            dog5.setOwnerEmail("alex.wilson@email.com");
            dog5.setBirthDate(LocalDate.now().minusYears(4));
            dog5.setMedicalNotes("Recently treated for minor injury");
            dog5.setStatus(Dog.DogStatus.Active);
            dogRepository.save(dog5);
        }
    }
}

