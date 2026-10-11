package com.library.librarymanagement.controller;

import com.library.librarymanagement.dto.LoginRequest;
import com.library.librarymanagement.dto.RegisterRequest;
import com.library.librarymanagement.dto.UserResponse;
import com.library.librarymanagement.entity.User;
import com.library.librarymanagement.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        User user = authService.register(request);
        UserResponse resp = new UserResponse();
        resp.setId(user.getId());
        resp.setName(user.getName());
        resp.setStudentClass(user.getStudentClass());
        resp.setSection(user.getSection());
        resp.setRollNo(user.getRollNo());
        resp.setUsername(user.getUsername());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        User user = authService.login(request);
        UserResponse resp = new UserResponse();
        resp.setId(user.getId());
        resp.setName(user.getName());
        resp.setStudentClass(user.getStudentClass());
        resp.setSection(user.getSection());
        resp.setRollNo(user.getRollNo());
        resp.setUsername(user.getUsername());
        session.setAttribute("userId", user.getId());
        return ResponseEntity.ok(resp);
    }
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        User user = authService.getUserById(userId);
        UserResponse resp = new UserResponse();
        resp.setId(user.getId());
        resp.setName(user.getName());
        resp.setStudentClass(user.getStudentClass());
        resp.setSection(user.getSection());
        resp.setRollNo(user.getRollNo());
        resp.setUsername(user.getUsername());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }
}
