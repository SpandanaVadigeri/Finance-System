package com.financeProject.MyProject.controller;

import com.financeProject.MyProject.dto.UserRequestDTO;
import com.financeProject.MyProject.dto.UserResponseDTO;
import com.financeProject.MyProject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/users") // Base URL for user APIs
public class UserController {

    @Autowired
    private UserService userService;

    // Get user by ID
    // GET /users/{id}
    @GetMapping("/{id}")
    public UserResponseDTO getUser(@PathVariable Long id) {

        return userService.getUser(id);
    }

    //here we send status, and that'll be assigned
    @PutMapping("/{id}/status")
    public UserResponseDTO updateStatus(@PathVariable Long id,
                                        @RequestParam String status) {

        return userService.updateStatus(id, status);
    }


}
