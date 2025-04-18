package com.dwij.trainbooking.service;

import com.dwij.trainbooking.models.User;

public interface UserService {
    void addUser(User user);

    User getUserByEmail(String email);
}