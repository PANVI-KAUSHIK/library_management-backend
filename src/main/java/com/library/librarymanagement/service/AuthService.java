package com.library.librarymanagement.service;

import com.library.librarymanagement.dto.RegisterRequest;
import com.library.librarymanagement.dto.LoginRequest;
import com.library.librarymanagement.entity.User;
import com.library.librarymanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(RegisterRequest request) {
        // Check duplicate username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        // Check duplicate student identifier
        if (userRepository.existsByStudentClassAndSectionAndRollNo(
                request.getStudentClass(), request.getSection(), request.getRollNo())) {
            throw new IllegalArgumentException("Student identifier already exists");
        }
        User user = new User();
        user.setName(request.getName());
        user.setStudentClass(request.getStudentClass());
        user.setSection(request.getSection());
        user.setRollNo(request.getRollNo());
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);
    }

    public User login(LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        User user = optionalUser.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return user;
    }
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

}
