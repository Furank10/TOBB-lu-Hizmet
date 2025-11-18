package com.tobblu.hizmet.controller;

import com.tobblu.hizmet.entity.User;
import com.tobblu.hizmet.entity.Skill;
import com.tobblu.hizmet.repository.UserRepository;
import com.tobblu.hizmet.repository.SkillRepository;
import com.tobblu.hizmet.util.JwtUtil;
import com.tobblu.hizmet.dto.SkillUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // Endpoint: GET /api/profile/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }
    
    // Endpoint: PUT /api/profile/skills
    @PutMapping("/skills")
    public ResponseEntity<?> updateSkills(@RequestBody SkillUpdateRequest request, 
                                          @RequestHeader("Authorization") String authHeader) {
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: Missing Token");
        }
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        for (String skillName : request.skills()) {
            Optional<Skill> existingSkillBox = skillRepository.findByName(skillName);
            if (existingSkillBox.isPresent()) {
                user.getSkills().add(existingSkillBox.get());
            } 
            else {
                Skill newSkill = new Skill();
                newSkill.setName(skillName);
                skillRepository.save(newSkill); 
                user.getSkills().add(newSkill);
            }
        }
        userRepository.save(user);
        return ResponseEntity.ok("Skills updated successfully!");
    }
}