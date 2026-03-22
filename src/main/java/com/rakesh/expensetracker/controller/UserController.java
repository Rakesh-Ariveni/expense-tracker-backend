package com.rakesh.expensetracker.controller;

import com.rakesh.expensetracker.dto.UserRequest;
import com.rakesh.expensetracker.dto.UserResponse;
import com.rakesh.expensetracker.entity.User;
import com.rakesh.expensetracker.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest req) {

        log.info("API HIT: Register User email={}", req.getEmail());

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(req.getPassword());

        User saved = userService.registerUser(user);

        UserResponse resp = new UserResponse();
        resp.setId(saved.getId());
        resp.setName(saved.getName());
        resp.setEmail(saved.getEmail());
        resp.setCreatedAt(saved.getCreatedAt());

        log.info("User registered successfully id={}", saved.getId());

        return ResponseEntity.status(201).body(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {

        log.info("API HIT: Get User id={}", id);

        User user = userService.getUserById(id);

        UserResponse resp = new UserResponse();
        resp.setId(user.getId());
        resp.setName(user.getName());
        resp.setEmail(user.getEmail());
        resp.setCreatedAt(user.getCreatedAt());

        log.info("User fetched successfully id={}", id);

        return ResponseEntity.ok(resp);
    }
}