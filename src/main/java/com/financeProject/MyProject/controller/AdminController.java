package com.financeProject.MyProject.controller;

import com.financeProject.MyProject.dto.UserRequestDTO;
import com.financeProject.MyProject.dto.UserResponseDTO;
import com.financeProject.MyProject.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    // GET /admin/users → list all users
    @GetMapping("/users")
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    // POST /admin/users → create user (ADMIN only)
    @PostMapping("/users")
    public UserResponseDTO createUser(@RequestBody UserRequestDTO dto,
                                      java.security.Principal principal) {

        String email = principal.getName();

        return userService.createUser(dto, email);
    }

    // GET /admin/users/{id}
    @GetMapping("/users/{id}")
    public UserResponseDTO getUserById(@PathVariable Long id) {
        return userService.getUser(id);
    }

    // PATCH /admin/users/{id}/status
    @PatchMapping("/users/{id}/status")
    public UserResponseDTO updateUserStatus(@PathVariable Long id,
                                            @RequestParam String status,
                                            java.security.Principal principal) {

        String adminEmail = principal.getName();

        return userService.updateUserStatus(id, status, adminEmail);
    }

    // PATCH /admin/users/{id}/role
    @PatchMapping("/users/{id}/role")
    public UserResponseDTO updateUserRole(@PathVariable Long id,
                                          @RequestParam String role,
                                          java.security.Principal principal) {

        String adminEmail = principal.getName();

        return userService.updateUserRole(id, role, adminEmail);
    }

}