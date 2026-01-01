package com.rakesh.expensetracker.repository;

import com.rakesh.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // for login/authentication
    boolean existsByEmail(String email);
}
