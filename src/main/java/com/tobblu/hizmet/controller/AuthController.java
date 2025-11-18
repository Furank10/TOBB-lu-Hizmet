package com.tobblu.hizmet.controller;

import java.util.Map;
import com.tobblu.hizmet.entity.User;
import com.tobblu.hizmet.util.JwtUtil;
import com.tobblu.hizmet.util.SecurityUtil;
import com.tobblu.hizmet.dto.LoginRequest;
import com.tobblu.hizmet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SecurityUtil securityUtil;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already taken");
        }
        user.setPassword(securityUtil.hashPassword(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        System.out.println("LOGIN ATTEMPT: username = " + request.username() + ", password = " + request.password());
        
        User user = userRepository.findByUsername(request.username()).orElse(null);
        
        if (user == null) {
            System.out.println("USER NOT FOUND IN DB");
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
        
        boolean passwordMatch = securityUtil.checkPassword(request.password(), user.getPassword());
        System.out.println("PASSWORD MATCH: " + passwordMatch);
        
        if (!passwordMatch) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
        
        String token = jwtUtil.generateToken(user.getUsername());
        
        return ResponseEntity.ok(Map.of("token", token, "id", user.getId()));
    }
}