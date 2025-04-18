package com.dwij.trainbooking.repository;

import com.dwij.trainbooking.exception.UserAlreadyExistsException;
import com.dwij.trainbooking.exception.UserNotFoundException;
import com.dwij.trainbooking.models.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepository {
    private final Map<String, User> users = new HashMap<>();

    public void save(User user) {
        if (users.containsKey(user.getEmail())) {
            throw new UserAlreadyExistsException("A user with this email already exists: " + user.getEmail());
        }
        users.put(user.getEmail(), user);
    }

    public User findByEmail(String email) {
        User user = users.get(email);
        if (user == null) {
            throw new UserNotFoundException("User with email " + email + " does not exist.");
        }
        return user;
    }

    public Map<String, User> findAll() {
        return users;
    }
}