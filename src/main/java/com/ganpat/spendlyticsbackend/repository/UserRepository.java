package com.ganpat.spendlyticsbackend.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ganpat.spendlyticsbackend.entity.User;


public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
