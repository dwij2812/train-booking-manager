package com.dwij.trainbooking.repository;

import com.dwij.trainbooking.exception.TicketAlreadyExistsException;
import com.dwij.trainbooking.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TicketRepositoryTest {
    private TicketRepository ticketRepository;

    @BeforeEach
    void setUp() {
        ticketRepository = new TicketRepository();
    }

    @Test
    void shouldSaveTicketSuccessfully() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
        Ticket ticket = Ticket.builder()
                .id("1")
                .user(user)
                .from("London")
                .to("France")
                .pricePaid(20.0)
                .seat(new Seat("A1", Section.A))
                .build();

        ticketRepository.save(ticket);

        Ticket retrievedTicket = ticketRepository.findByUserEmail("john.doe@example.com");
        assertNotNull(retrievedTicket);
        assertEquals("London", retrievedTicket.getFrom());
        assertEquals("France", retrievedTicket.getTo());
        assertEquals(20.0, retrievedTicket.getPricePaid());
        assertEquals("A1", retrievedTicket.getSeat().getSeatNumber());
    }

    @Test
    void shouldThrowExceptionWhenSavingDuplicateTicket() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
        Ticket ticket = Ticket.builder()
                .id("1")
                .user(user)
                .from("London")
                .to("France")
                .pricePaid(20.0)
                .seat(new Seat("A1", Section.A))
                .build();

        ticketRepository.save(ticket);

        TicketAlreadyExistsException exception = assertThrows(TicketAlreadyExistsException.class, () ->
                ticketRepository.save(ticket));

        assertEquals("A ticket is already booked for this email: john.doe@example.com", exception.getMessage());
    }

    @Test
    void shouldFindTicketByUserEmailSuccessfully() {
        User user = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .build();
        Ticket ticket = Ticket.builder()
                .id("2")
                .user(user)
                .from("Paris")
                .to("Berlin")
                .pricePaid(25.0)
                .seat(new Seat("B1", Section.B))
                .build();

        ticketRepository.save(ticket);

        Ticket retrievedTicket = ticketRepository.findByUserEmail("jane.smith@example.com");
        assertNotNull(retrievedTicket);
        assertEquals("Paris", retrievedTicket.getFrom());
        assertEquals("Berlin", retrievedTicket.getTo());
        assertEquals(25.0, retrievedTicket.getPricePaid());
        assertEquals("B1", retrievedTicket.getSeat().getSeatNumber());
    }

    @Test
    void shouldReturnNullWhenTicketNotFoundByUserEmail() {
        Ticket retrievedTicket = ticketRepository.findByUserEmail("nonexistent@example.com");
        assertNull(retrievedTicket);
    }

    @Test
    void shouldDeleteTicketSuccessfully() {
        User user = User.builder()
                .firstName("Alice")
                .lastName("Brown")
                .email("alice.brown@example.com")
                .build();
        Ticket ticket = Ticket.builder()
                .id("3")
                .user(user)
                .from("Rome")
                .to("Madrid")
                .pricePaid(30.0)
                .seat(new Seat("C1", Section.A))
                .build();

        ticketRepository.save(ticket);
        ticketRepository.deleteByUserEmail("alice.brown@example.com");

        Ticket retrievedTicket = ticketRepository.findByUserEmail("alice.brown@example.com");
        assertNull(retrievedTicket);
    }
}