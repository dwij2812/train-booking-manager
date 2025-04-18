package com.dwij.trainbooking.service.impl;

import com.dwij.trainbooking.exception.UserAlreadyExistsException;
import com.dwij.trainbooking.exception.UserNotFoundException;
import com.dwij.trainbooking.models.User;
import com.dwij.trainbooking.repository.UserRepository;
import com.dwij.trainbooking.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void shouldAddUserSuccessfully() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        doNothing().when(userRepository).save(user);

        userService.addUser(user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyExists() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        doThrow(new UserAlreadyExistsException("A user with this email already exists: john.doe@example.com"))
                .when(userRepository).save(user);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> userService.addUser(user));

        assertEquals("A user with this email already exists: john.doe@example.com", exception.getMessage());
    }

    @Test
    void shouldRetrieveUserByEmailSuccessfully() {
        User user = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .build();

        when(userRepository.findByEmail("jane.smith@example.com")).thenReturn(user);

        User retrievedUser = userService.getUserByEmail("jane.smith@example.com");

        assertNotNull(retrievedUser);
        assertEquals("Jane", retrievedUser.getFirstName());
        assertEquals("Smith", retrievedUser.getLastName());
        assertEquals("jane.smith@example.com", retrievedUser.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(null);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.getUserByEmail("john.doe@example.com"));

        assertEquals("User with email john.doe@example.com does not exist.", exception.getMessage());
    }
}