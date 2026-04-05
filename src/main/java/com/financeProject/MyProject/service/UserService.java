package com.financeProject.MyProject.service;

import com.financeProject.MyProject.dto.UserRequestDTO;
import com.financeProject.MyProject.dto.UserResponseDTO;
import com.financeProject.MyProject.model.Role;
import com.financeProject.MyProject.model.User;
import com.financeProject.MyProject.repository.RoleRepository;
import com.financeProject.MyProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    // CREATE USER
    // Called from: POST /users
    public UserResponseDTO createUser(UserRequestDTO dto, String currentUserEmail) {

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Allowing only admin
        if (!currentUser.getRole().getName().equals("ADMIN")) {
            throw new RuntimeException("Only ADMIN can create users");
        }

        // Block ADMIN Creation
        if (dto.getRole().equals("ADMIN")) {
            throw new RuntimeException("Cannot create ADMIN");
        }


        // Check if email already exists
        Optional<User> existing = userRepository.findByEmail(dto.getEmail());
        if (existing.isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Fetch role from DB (VIEWER / ANALYST / ADMIN)
        Role role = roleRepository.findByName(dto.getRole())
                .orElseThrow(() -> new RuntimeException("Invalid role"));
        if (dto.getRole().equals("ADMIN")) {
            throw new RuntimeException("Cannot create ADMIN via API");
        } // To block creating any other admin

        // Convert DTO → Entity
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));// (later can hash)
        user.setRole(role);
        user.setStatus("ACTIVE");

        // Save user
        User savedUser = userRepository.save(user);

        // Convert Entity → Response DTO
        return convertToDTO(savedUser);
    }

    // GET ALL USERS
    public List<UserResponseDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    //  GET USER BY ID
    // Called from: GET /users/{id}
    public UserResponseDTO getUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return convertToDTO(user);
    }

    public UserResponseDTO updateUserStatus(Long userId, String status, String adminEmail) {

        // Get current user (who is making request)
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        // Ensure only ADMIN can update
        if (!admin.getRole().getName().equals("ADMIN")) {
            throw new RuntimeException("Only ADMIN can update user status");
        }

        // Get target user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Optional: prevent deactivating ADMIN
        if (user.getRole().getName().equals("ADMIN")) {
            throw new RuntimeException("Cannot modify ADMIN status");
        }

        // Validate status input
        if (!status.equals("ACTIVE") && !status.equals("INACTIVE")) {
            throw new RuntimeException("Invalid status");
        }

        // Update
        user.setStatus(status);

        User updated = userRepository.save(user);

        return convertToDTO(updated);
    }

    public UserResponseDTO updateUserRole(Long userId, String roleName, String adminEmail) {

        //  Get current user (who is making request)
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        //  Only ADMIN allowed
        if (!admin.getRole().getName().equals("ADMIN")) {
            throw new RuntimeException("Only ADMIN can update roles");
        }

        // Get target user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Prevent modifying ADMIN user
        if (user.getRole().getName().equals("ADMIN")) {
            throw new RuntimeException("Cannot modify ADMIN user");
        }

        // Prevent assigning ADMIN role
        if (roleName.equals("ADMIN")) {
            throw new RuntimeException("Cannot assign ADMIN role");
        }

        // Get role from DB
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Update role
        user.setRole(role);

        User updated = userRepository.save(user);

        return convertToDTO(updated);
    }


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
