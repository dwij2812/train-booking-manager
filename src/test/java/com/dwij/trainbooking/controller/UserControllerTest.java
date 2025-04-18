package com.dwij.trainbooking.controller;

import com.dwij.trainbooking.models.User;
import com.dwij.trainbooking.service.UserService;
import com.dwij.trainbooking.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        Mockito.reset(userService);
    }

    @Test
    void shouldAddUserSuccessfully() throws Exception {
        doNothing().when(userService).addUser(any(User.class));
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
        verify(userService, times(1)).addUser(any(User.class));
    }

    @Test
    void shouldGetUserByEmailSuccessfully() throws Exception {
        String email = "john.doe@example.com";
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email(email)
                .build();

        when(userService.getUserByEmail(email)).thenReturn(user);

        mockMvc.perform(get("/api/users/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value(email));

        verify(userService, times(1)).getUserByEmail(email);
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        String email = "nonexistent@example.com";

        when(userService.getUserByEmail(email)).thenThrow(new com.dwij.trainbooking.exception.UserNotFoundException("User not found"));

        mockMvc.perform(get("/api/users/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserByEmail(email);
    }
}