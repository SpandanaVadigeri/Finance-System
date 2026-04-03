package com.financeProject.MyProject.service;

import com.financeProject.MyProject.dto.UserRequestDTO;
import com.financeProject.MyProject.dto.UserResponseDTO;
import com.financeProject.MyProject.model.Role;
import com.financeProject.MyProject.model.User;
import com.financeProject.MyProject.repository.RoleRepository;
import com.financeProject.MyProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    // CREATE USER
    // Called from: POST /users
    public UserResponseDTO createUser(UserRequestDTO dto) {

        // Check if email already exists
        Optional<User> existing = userRepository.findByEmail(dto.getEmail());
        if (existing.isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Fetch role from DB (VIEWER / ANALYST / ADMIN)
        Role role = roleRepository.findByName(dto.getRole())
                .orElseThrow(() -> new RuntimeException("Invalid role"));

        // Convert DTO → Entity
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // (later can hash)
        user.setRole(role);
        user.setStatus("ACTIVE");

        // Save user
        User savedUser = userRepository.save(user);

        // Convert Entity → Response DTO
        return convertToDTO(savedUser);
    }

    //  GET USER BY ID
    // Called from: GET /users/{id}
    public UserResponseDTO getUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return convertToDTO(user);
    }

//    // UPDATE USER ROLE
//    // Called from: PUT /users/{id}/role
//    public UserResponseDTO updateUserRole(Long userId, String roleName) {
//
//        // Fetch user
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // Fetch role
//        Role role = roleRepository.findByName(roleName)
//                .orElseThrow(() -> new RuntimeException("Role not found"));
//
//        // Update role
//        user.setRole(role);
//
//        // Save updated user
//        User updatedUser = userRepository.save(user);
//
//        return convertToDTO(updatedUser);
//    }
//
//    // (OPTIONAL BUT GOOD) ACTIVATE / DEACTIVATE USER
//    public UserResponseDTO updateUserStatus(Long userId, String status) {
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        user.setStatus(status); // ACTIVE / INACTIVE
//
//        User updated = userRepository.save(user);
//
//        return convertToDTO(updated);
//    }

    // HELPER METHOD: ENTITY → DTO
    // Centralized mapping (clean design)
    private UserResponseDTO convertToDTO(User user) {

        UserResponseDTO dto = new UserResponseDTO();

        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());

        // Important: Role is entity → we return role name
        dto.setRole(user.getRole().getName());

        dto.setStatus(user.getStatus());

        return dto;
    }
}
