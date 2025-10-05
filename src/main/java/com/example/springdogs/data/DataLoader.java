package com.example.springdogs.data;

import com.example.springdogs.model.User;
import com.example.springdogs.model.Dog;
import com.example.springdogs.repository.UserRepository;
import com.example.springdogs.repository.DogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
            System.out.println("Admin user created");
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
            System.out.println("Guest user created");
        }

        // Add sample dogs for testing
        if (dogRepository.count() == 0) {
            Dog dog1 = new Dog();
            dog1.setName("Buddy");
            dog1.setBreed("Golden Retriever");
            dog1.setAge(3);
            dog1.setColor("Golden");
            dog1.setWeight(70.5);
            dog1.setTemperament("Everyone's best friend - gentle, loving, and great with kids");
            dog1.setIsSafeToPet("Yes");
            dog1.setSafetyExplanation("Golden Retrievers are known for their gentle temperament. This dog shows typical friendly behavior and poses no safety risks.");
            dogRepository.save(dog1);

            Dog dog2 = new Dog();
            dog2.setName("Luna");
            dog2.setBreed("German Shepherd");
            dog2.setAge(5);
            dog2.setColor("Black & Tan");
            dog2.setWeight(85.2);
            dog2.setTemperament("Highly intelligent working dog - loyal, protective, and energetic");
            dog2.setIsSafeToPet("Cautiously");
            dog2.setSafetyExplanation("German Shepherds are naturally protective. While intelligent and loyal, they can be guarded around strangers and unpredictable.");
            dogRepository.save(dog2);

            Dog dog3 = new Dog();
            dog3.setName("Max");
            dog3.setBreed("Labrador Retriever");
            dog3.setAge(7);
            dog3.setColor("Chocolate");
            dog3.setWeight(65.0);
            dog3.setTemperament("Gentle giant who loves children, water activities, and fetch");
            dog3.setIsSafeToPet("Yes");
            dog3.setSafetyExplanation("Labrador Retrievers are known for their friendly nature. This dog shows excellent temperament around children and is well-socialized.");
            dogRepository.save(dog3);

            Dog dog4 = new Dog();
            dog4.setName("Bella");
            dog4.setBreed("Beagle");
            dog4.setAge(2);
            dog4.setColor("Tri-color");
            dog4.setWeight(25.8);
            dog4.setTemperament("Playful, curious, and excellent with families - loves to sniff and explore");
            dog4.setIsSafeToPet("Yes");
            dog4.setSafetyExplanation("Beagles are typically friendly and great with families. This dog shows normal playful behavior and poses minimal risk.");
            dogRepository.save(dog4);

            Dog dog5 = new Dog();
            dog5.setName("Rocky");
            dog5.setBreed("Boxer");
            dog5.setAge(4);
            dog5.setColor("Brindle");
            dog5.setWeight(75.3);
            dog5.setTemperament("Territorial, aggressive. Not trained or socialized. Multiple bites on record");
            dog5.setIsSafeToPet("No");
            dog5.setSafetyExplanation("This dog has a documented history of aggression and bites. Do not approach under any circumstances. Requires professional training.");
            dogRepository.save(dog5);

            Dog dog6 = new Dog();
            dog6.setName("Charlie");
            dog6.setBreed("Siberian Husky");
            dog6.setAge(1);
            dog6.setColor("White & Gray");
            dog6.setWeight(55.1);
            dog6.setTemperament("High energy sled dog - independent, intelligent, needs lots of exercise");
            dog6.setIsSafeToPet("Cautiously");
            dog6.setSafetyExplanation("Huskies are independent working dogs with high energy. Can be unpredictable and may not respond well to strangers approaching.");
            dogRepository.save(dog6);

            Dog dog7 = new Dog();
            dog7.setName("Molly");
            dog7.setBreed("French Bulldog");
            dog7.setAge(6);
            dog7.setColor("Fawn");
            dog7.setWeight(28.4);
            dog7.setTemperament("Chill couch potato - calm, friendly, great apartment companion");
            dog7.setIsSafeToPet("Yes");
            dog7.setSafetyExplanation("French Bulldogs are typically calm and friendly. This dog shows relaxed temperament and poses no safety concerns.");
            dogRepository.save(dog7);

            Dog dog8 = new Dog();
            dog8.setName("Zeus");
            dog8.setBreed("Great Dane");
            dog8.setAge(3);
            dog8.setColor("Black");
            dog8.setWeight(145.7);
            dog8.setTemperament("Gentle giant - despite massive size, super sweet and calm with kids");
            dog8.setIsSafeToPet("Yes");
            dog8.setSafetyExplanation("Great Danes are known as gentle giants. Despite their size, this dog shows excellent temperament and is great with children.");
            dogRepository.save(dog8);
            
            System.out.println("Sample dogs created successfully");
        }
    }
}