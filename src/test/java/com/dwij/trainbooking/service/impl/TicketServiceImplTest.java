package com.dwij.trainbooking.service.impl;

import com.dwij.trainbooking.exception.SeatUnavailableException;
import com.dwij.trainbooking.exception.TicketAlreadyExistsException;
import com.dwij.trainbooking.exception.UserNotFoundException;
import com.dwij.trainbooking.models.*;
import com.dwij.trainbooking.repository.TicketRepository;
import com.dwij.trainbooking.service.TicketService;
import com.dwij.trainbooking.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketServiceImplTest {
    private TicketRepository ticketRepository;
    private SimpleSeatAllocationService seatAllocationService;
    private UserService userService;
    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        ticketRepository = mock(TicketRepository.class);
        seatAllocationService = mock(SimpleSeatAllocationService.class);
        userService = mock(UserService.class);
        ticketService = new TicketServiceImpl(ticketRepository, seatAllocationService, userService);
    }

    @Test
    void shouldPurchaseTicketSuccessfully() {
        TicketRequest request = new TicketRequest("John", "Doe", "john.doe@example.com", "London", "France", Section.A);
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
        Seat seat = new Seat("A1", Section.A);

        when(ticketRepository.findByUserEmail("john.doe@example.com")).thenReturn(null);
        when(seatAllocationService.allocateSeat(Section.A)).thenReturn(seat);
        when(userService.getUserByEmail("john.doe@example.com")).thenThrow(new UserNotFoundException("User not found"));
        doNothing().when(userService).addUser(user);

        Ticket ticket = ticketService.purchaseTicket(request);

        assertNotNull(ticket);
        assertEquals("London", ticket.getFrom());
        assertEquals("France", ticket.getTo());
        assertEquals(20.0, ticket.getPricePaid());
        assertEquals(seat, ticket.getSeat());
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    void shouldThrowExceptionWhenTicketAlreadyExists() {
        TicketRequest request = new TicketRequest("John", "Doe", "john.doe@example.com", "London", "France", Section.A);

        when(ticketRepository.findByUserEmail("john.doe@example.com")).thenReturn(Ticket.builder().build());

        TicketAlreadyExistsException exception = assertThrows(TicketAlreadyExistsException.class,
                () -> ticketService.purchaseTicket(request));

        assertEquals("A ticket is already booked for this email: john.doe@example.com", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNoSeatsAvailable() {
        TicketRequest request = new TicketRequest("John", "Doe", "john.doe@example.com", "London", "France", Section.A);

        when(ticketRepository.findByUserEmail("john.doe@example.com")).thenReturn(null);
        when(seatAllocationService.allocateSeat(Section.A)).thenReturn(null);

        SeatUnavailableException exception = assertThrows(SeatUnavailableException.class,
                () -> ticketService.purchaseTicket(request));

        assertEquals("No seats available in section: A", exception.getMessage());
    }

    @Test
    void shouldCancelTicketSuccessfully() {
        Ticket ticket = Ticket.builder()
                .id("1")
                .user(User.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .email("john.doe@example.com")
                        .build())
                .from("London")
                .to("France")
                .pricePaid(20.0)
                .seat(new Seat("A1", Section.A))
                .build();

        when(ticketRepository.findByUserEmail("john.doe@example.com")).thenReturn(ticket);

        ticketService.cancelTicket("john.doe@example.com");

        verify(seatAllocationService, times(1)).releaseSeat(ticket.getSeat());
        verify(ticketRepository, times(1)).deleteByUserEmail("john.doe@example.com");
    }

    @Test
    void shouldModifySeatSuccessfully() {
        Ticket ticket = Ticket.builder()
                .id("1")
                .user(User.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .email("john.doe@example.com")
                        .build())
                .from("London")
                .to("France")
                .pricePaid(20.0)
                .seat(new Seat("A1", Section.A))
                .build();

        Seat requestedSeat = new Seat("B1", Section.B);

        when(ticketRepository.findByUserEmail("john.doe@example.com")).thenReturn(ticket);
        when(seatAllocationService.isSeatAvailable(requestedSeat)).thenReturn(true);
        when(seatAllocationService.reallocateSeat(ticket.getSeat(), requestedSeat)).thenReturn(requestedSeat);

        Ticket updatedTicket = ticketService.modifySeat("john.doe@example.com", requestedSeat);

        assertNotNull(updatedTicket);
        assertEquals("B1", updatedTicket.getSeat().getSeatNumber());
        assertEquals(Section.B, updatedTicket.getSeat().getSection());
        verify(ticketRepository, times(1)).save(updatedTicket);
    }

    @Test
    void shouldReturnUsersAndSeatsBySection() {
        Section section = Section.A;

        Ticket ticket1 = Ticket.builder()
                .id("1")
                .user(User.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .email("john.doe@example.com")
                        .build())
                .seat(new Seat("A1", Section.A))
                .build();

        Ticket ticket2 = Ticket.builder()
                .id("2")
                .user(User.builder()
                        .firstName("Jane")
                        .lastName("Smith")
                        .email("jane.smith@example.com")
                        .build())
                .seat(new Seat("A2", Section.A))
                .build();

        when(ticketRepository.findAll()).thenReturn(Map.of(
                "1", ticket1,
                "2", ticket2));

        List<String> result = ticketService.getUsersAndSeatsBySection(section);

        assertEquals(2, result.size());
        assertEquals("User: john.doe@example.com, Seat: A1", result.get(0));
        assertEquals("User: jane.smith@example.com, Seat: A2", result.get(1));
    }

    @Test
    void shouldReturnEmptyListWhenNoUsersInSection() {
        Section section = Section.B;

        when(ticketRepository.findAll()).thenReturn(Map.of());

        List<String> result = ticketService.getUsersAndSeatsBySection(section);

        assertEquals(0, result.size());
    }
}