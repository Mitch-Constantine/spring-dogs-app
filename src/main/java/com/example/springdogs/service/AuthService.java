package com.example.springdogs.service;

import com.example.springdogs.dto.LoginRequest;
import com.example.springdogs.dto.LoginResponse;
import com.example.springdogs.dto.UserDto;
import com.example.springdogs.model.User;
import com.example.springdogs.repository.UserRepository;
import com.example.springdogs.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                                 .orElse(null);
        
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.getActive()) {
            throw new RuntimeException("User account is deactivated");
        }

        String token = jwtUtils.generateToken(user.getUsername());
        
        return new LoginResponse(token, "Bearer", UserDto.fromEntity(user));
    }

}

