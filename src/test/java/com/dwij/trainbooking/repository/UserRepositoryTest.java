package com.dwij.trainbooking.repository;

import com.dwij.trainbooking.exception.UserAlreadyExistsException;
import com.dwij.trainbooking.exception.UserNotFoundException;
import com.dwij.trainbooking.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
    }

    @Test
    void shouldSaveUserSuccessfully() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        userRepository.save(user);

        User retrievedUser = userRepository.findByEmail("john.doe@example.com");
        assertNotNull(retrievedUser);
        assertEquals("John", retrievedUser.getFirstName());
        assertEquals("Doe", retrievedUser.getLastName());
        assertEquals("john.doe@example.com", retrievedUser.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenSavingDuplicateUser() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
        userRepository.save(user);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () ->
                userRepository.save(user));

        assertEquals("A user with this email already exists: john.doe@example.com", exception.getMessage());
    }

    @Test
    void shouldFindUserByEmailSuccessfully() {
        User user = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .build();
        userRepository.save(user);

        User retrievedUser = userRepository.findByEmail("jane.smith@example.com");

        assertNotNull(retrievedUser);
        assertEquals("Jane", retrievedUser.getFirstName());
        assertEquals("Smith", retrievedUser.getLastName());
        assertEquals("jane.smith@example.com", retrievedUser.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                userRepository.findByEmail("nonexistent@example.com"));

        assertEquals("User with email nonexistent@example.com does not exist.", exception.getMessage());
    }
}