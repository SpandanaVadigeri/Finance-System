package com.financeProject.MyProject.dto;
import org.springframework.security.crypto.password.PasswordEncoder;
public class AuthResponseDTO {

    private String token;

    public AuthResponseDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}