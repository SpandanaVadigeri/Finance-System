package com.financeProject.MyProject.controller;
    import com.financeProject.MyProject.dto.AuthRequestDTO;
    import com.financeProject.MyProject.dto.AuthResponseDTO;
    import com.financeProject.MyProject.security.JwtUtil;
    import com.financeProject.MyProject.service.AuthService;
    import jakarta.servlet.http.HttpServletRequest;
    import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
    @RestController
    @RequestMapping("/auth") // Base path for authentication
    public class AuthController {

        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private JwtUtil jwtUtil;

        @Autowired
        private AuthService authService;

        // LOGIN API
        // POST /auth/login
        @PostMapping("/login")
        public AuthResponseDTO login(@RequestBody AuthRequestDTO request) {

            // Step 1: Authenticate user (email + password)
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // Step 2: Generate JWT token
            String token = jwtUtil.generateToken(request.getEmail());

            // Step 3: Return token
            return new AuthResponseDTO(token);
        }

        @PostMapping("/logout")
        public String logout(HttpServletRequest request) {

            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {

                String token = authHeader.substring(7);

                authService.logout(token);

                return "Logged out successfully";
            }

            return "No token provided";
        }
    }



