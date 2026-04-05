package com.financeProject.MyProject.repository;

import com.financeProject.MyProject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Used for login or checking duplicate email
    Optional<User> findByEmail(String email);

    List<User> findByRoleName(String roleName);
}