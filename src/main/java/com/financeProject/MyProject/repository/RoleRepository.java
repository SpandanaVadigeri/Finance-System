package com.financeProject.MyProject.repository;

import com.financeProject.MyProject.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// JpaRepository<Role, Long>
// Role = entity
// Long = type of primary key
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Custom method to find role by name (VIEWER, ANALYST, ADMIN)
    Optional<Role> findByName(String name);
}
