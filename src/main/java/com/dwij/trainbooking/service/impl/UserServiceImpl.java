package com.dwij.trainbooking.service.impl;

import com.dwij.trainbooking.exception.UserNotFoundException;
import com.dwij.trainbooking.models.User;
import com.dwij.trainbooking.repository.UserRepository;
import com.dwij.trainbooking.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void addUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User with email " + email + " does not exist.");
        }
        return user;
    }
}