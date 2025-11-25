package com.rakesh.expensetracker.service;

import com.rakesh.expensetracker.entity.User;
import java.util.Optional;

public interface UserService {

    User registerUser(User user);

    Optional<User> getUserByEmail(String email);

    User getUserById(Long id);
}
