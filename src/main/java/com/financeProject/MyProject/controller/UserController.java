package com.financeProject.MyProject.controller;

import com.financeProject.MyProject.dto.UserRequestDTO;
import com.financeProject.MyProject.dto.UserResponseDTO;
import com.financeProject.MyProject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users") // Base URL for user APIs
public class UserController {

    @Autowired
    private UserService userService;

    // Create new user
    // POST /users
    @PostMapping
    public UserResponseDTO createUser(@RequestBody UserRequestDTO requestDTO) {

        // Controller simply forwards request to service layer
        return userService.createUser(requestDTO);
    }

    // Get user by ID
    // GET /users/{id}
    @GetMapping("/{id}")
    public UserResponseDTO getUser(@PathVariable Long id) {

        return userService.getUser(id);
    }

}
