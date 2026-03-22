package com.rakesh.expensetracker.service.impl;

import com.rakesh.expensetracker.entity.User;
import com.rakesh.expensetracker.exception.ResourceNotFoundException;
import com.rakesh.expensetracker.repository.UserRepository;
import com.rakesh.expensetracker.service.UserService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User registerUser(User user) {

        log.info("Registering user email={}", user.getEmail());

        User saved = userRepository.save(user);

        log.info("User registered successfully id={}", saved.getId());

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {

        log.debug("Fetching user by email={}", email);

        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {

        log.info("Fetching user by id={}", id);

        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found id={}", id);
                    return new ResourceNotFoundException("User not found: " + id);
                });
    }
}