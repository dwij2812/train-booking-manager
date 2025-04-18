package com.dwij.trainbooking.controller;

import com.dwij.trainbooking.exception.GlobalExceptionHandler;
import com.dwij.trainbooking.exception.TicketNotFoundException;
import com.dwij.trainbooking.models.Seat;
import com.dwij.trainbooking.models.Section;
import com.dwij.trainbooking.models.Ticket;
import com.dwij.trainbooking.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketController.class)
@Import(GlobalExceptionHandler.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        Mockito.reset(ticketService);
    }

    @Test
    void shouldPurchaseTicketSuccessfully() throws Exception {
        Ticket ticket = Ticket.builder()
                .id("1")
                .from("London")
                .to("France")
                .pricePaid(20.0)
                .seat(new Seat("A1", Section.A))
                .build();

        when(ticketService.purchaseTicket("john.doe@example.com", Section.A)).thenReturn(ticket);

        mockMvc.perform(post("/api/tickets/purchase")
                        .param("email", "john.doe@example.com")
                        .param("section", "A")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.from").value("London"))
                .andExpect(jsonPath("$.to").value("France"))
                .andExpect(jsonPath("$.pricePaid").value(20.0))
                .andExpect(jsonPath("$.seat.seatNumber").value("A1"))
                .andExpect(jsonPath("$.seat.section").value("A"));

        verify(ticketService, times(1)).purchaseTicket("john.doe@example.com", Section.A);
    }

    @Test
    void shouldGetReceiptSuccessfully() throws Exception {
        Ticket ticket = Ticket.builder()
                .id("1")
                .from("London")
                .to("France")
                .pricePaid(20.0)
                .seat(new Seat("A1", Section.A))
                .build();

        when(ticketService.getTicket("john.doe@example.com")).thenReturn(ticket);

        mockMvc.perform(get("/api/tickets/{email}/receipt", "john.doe@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.from").value("London"))
                .andExpect(jsonPath("$.to").value("France"))
                .andExpect(jsonPath("$.pricePaid").value(20.0))
                .andExpect(jsonPath("$.seat.seatNumber").value("A1"))
                .andExpect(jsonPath("$.seat.section").value("A"));

        verify(ticketService, times(1)).getTicket("john.doe@example.com");
    }

    @Test
    void shouldReturnNotFoundWhenReceiptNotFound() throws Exception {
        when(ticketService.getTicket("nonexistent@example.com")).thenThrow(
                new TicketNotFoundException("No ticket found for email: nonexistent@example.com"));

        mockMvc.perform(get("/api/tickets/{email}/receipt", "nonexistent@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(ticketService, times(1)).getTicket("nonexistent@example.com");
    }

    @Test
    void shouldRemoveUserFromTrainSuccessfully() throws Exception {
        doNothing().when(ticketService).cancelTicket("john.doe@example.com");

        mockMvc.perform(delete("/api/tickets/{email}/remove", "john.doe@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(ticketService, times(1)).cancelTicket("john.doe@example.com");
    }

    @Test
    void shouldModifySeatSuccessfully() throws Exception {
        Seat requestedSeat = new Seat("B1", Section.B);
        Ticket updatedTicket = Ticket.builder()
                .id("1")
                .from("London")
                .to("France")
                .pricePaid(20.0)
                .seat(requestedSeat)
                .build();

        when(ticketService.modifySeat("john.doe@example.com", requestedSeat)).thenReturn(updatedTicket);

        mockMvc.perform(put("/api/tickets/{email}/modify-seat", "john.doe@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seatNumber\":\"B1\",\"section\":\"B\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.from").value("London"))
                .andExpect(jsonPath("$.to").value("France"))
                .andExpect(jsonPath("$.pricePaid").value(20.0))
                .andExpect(jsonPath("$.seat.seatNumber").value("B1"))
                .andExpect(jsonPath("$.seat.section").value("B"));

        verify(ticketService, times(1)).modifySeat(eq("john.doe@example.com"), eq(requestedSeat));
    }
}