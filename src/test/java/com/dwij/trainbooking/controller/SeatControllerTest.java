package com.dwij.trainbooking.controller;

import com.dwij.trainbooking.models.Seat;
import com.dwij.trainbooking.models.Section;
import com.dwij.trainbooking.service.SeatAllocationStrategy;
import com.dwij.trainbooking.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SeatController.class)
class SeatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SeatAllocationStrategy seatAllocationService;

    @MockBean
    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        Mockito.reset(seatAllocationService, ticketService);
    }

    @Test
    void shouldReturnAvailableSeatsBySection() throws Exception {
        Section section = Section.A;
        List<Seat> availableSeats = Arrays.asList(
                new Seat("A1", Section.A),
                new Seat("A2", Section.A)
        );

        when(seatAllocationService.getAvailableSeats(section)).thenReturn(availableSeats);

        mockMvc.perform(get("/api/seats/available/A")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].seatNumber").value("A1"))
                .andExpect(jsonPath("$[0].section").value("A"))
                .andExpect(jsonPath("$[1].seatNumber").value("A2"))
                .andExpect(jsonPath("$[1].section").value("A"));
    }

    @Test
    void shouldReturnAllocatedSeatsBySection() throws Exception {
        Section section = Section.A;
        List<String> usersAndSeats = Arrays.asList(
                "User: john.doe@example.com, Seat: A1",
                "User: jane.smith@example.com, Seat: A2"
        );

        when(ticketService.getUsersAndSeatsBySection(section)).thenReturn(usersAndSeats);

        mockMvc.perform(get("/api/seats/allocated/A")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("User: john.doe@example.com, Seat: A1"))
                .andExpect(jsonPath("$[1]").value("User: jane.smith@example.com, Seat: A2"));
    }

    @Test
    void shouldReturnEmptyListWhenNoAvailableSeats() throws Exception {
        Section section = Section.B;

        when(seatAllocationService.getAvailableSeats(section)).thenReturn(List.of());

        mockMvc.perform(get("/api/seats/available/B")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenNoAllocatedSeats() throws Exception {
        Section section = Section.B;

        when(ticketService.getUsersAndSeatsBySection(section)).thenReturn(List.of());

        mockMvc.perform(get("/api/seats/allocated/B")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}