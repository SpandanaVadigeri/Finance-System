package com.financeProject.MyProject.service;

import com.financeProject.MyProject.entity.BlacklistedToken;
import com.financeProject.MyProject.repository.BlacklistedTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private BlacklistedTokenRepository blacklistRepo;

    public void logout(String token) {

        BlacklistedToken bt = new BlacklistedToken();
        bt.setToken(token);

        blacklistRepo.save(bt);
    }

    public boolean isBlacklisted(String token) {
        return blacklistRepo.existsByToken(token);
    }
}
