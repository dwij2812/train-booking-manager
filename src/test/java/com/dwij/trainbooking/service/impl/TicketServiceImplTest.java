package com.dwij.trainbooking.service.impl;

import com.dwij.trainbooking.exception.SeatUnavailableException;
import com.dwij.trainbooking.exception.TicketAlreadyExistsException;
import com.dwij.trainbooking.exception.TicketNotFoundException;
import com.dwij.trainbooking.models.*;
import com.dwij.trainbooking.repository.TicketRepository;
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
    private TicketServiceImpl ticketService;

    @BeforeEach
    void setUp() {
        ticketRepository = mock(TicketRepository.class);
        seatAllocationService = mock(SimpleSeatAllocationService.class);
        userService = mock(UserService.class);
        ticketService = new TicketServiceImpl(ticketRepository, seatAllocationService, userService);
    }

    @Test
    void shouldPurchaseTicketSuccessfully() {
        String email = "john.doe@example.com";
        Section section = Section.A;

        User user = User.builder()
                .email(email)
                .build();
        Seat seat = new Seat("A1", Section.A);

        when(ticketRepository.findByUserEmail(email)).thenReturn(null);
        when(seatAllocationService.allocateSeat(section)).thenReturn(seat);
        when(userService.getUserByEmail(email)).thenReturn(user);

        Ticket ticket = ticketService.purchaseTicket(email, section);

        assertNotNull(ticket);
        assertEquals("London", ticket.getFrom());
        assertEquals("France", ticket.getTo());
        assertEquals(20.0, ticket.getPricePaid());
        assertEquals(seat, ticket.getSeat());
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    void shouldThrowExceptionWhenTicketAlreadyExists() {
        String email = "john.doe@example.com";
        Section section = Section.A;

        when(ticketRepository.findByUserEmail(email)).thenReturn(Ticket.builder().build());

        TicketAlreadyExistsException exception = assertThrows(TicketAlreadyExistsException.class,
                () -> ticketService.purchaseTicket(email, section));

        assertEquals("A ticket is already booked for this email: " + email, exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNoSeatsAvailable() {
        String email = "john.doe@example.com";
        Section section = Section.A;

        when(ticketRepository.findByUserEmail(email)).thenReturn(null);
        when(seatAllocationService.allocateSeat(section)).thenThrow(new SeatUnavailableException("No seats available in section: " + section));

        SeatUnavailableException exception = assertThrows(SeatUnavailableException.class,
                () -> ticketService.purchaseTicket(email, section));

        assertEquals("No seats available in section: " + section, exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTicketNotFoundForCancelTicket() {
        String email = "nonexistent@example.com";

        when(ticketRepository.findByUserEmail(email)).thenReturn(null);

        TicketNotFoundException exception = assertThrows(TicketNotFoundException.class,
                () -> ticketService.cancelTicket(email));

        assertEquals("No ticket found for email: " + email, exception.getMessage());
    }

    @Test
    void shouldCancelTicketSuccessfully() {
        String email = "john.doe@example.com";

        Ticket ticket = Ticket.builder()
                .id("1")
                .user(User.builder()
                        .email(email)
                        .build())
                .from("London")
                .to("France")
                .pricePaid(20.0)
                .seat(new Seat("A1", Section.A))
                .build();

        when(ticketRepository.findByUserEmail(email)).thenReturn(ticket);

        ticketService.cancelTicket(email);

        verify(seatAllocationService, times(1)).releaseSeat(ticket.getSeat());
        verify(ticketRepository, times(1)).deleteByUserEmail(email);
    }

    @Test
    void shouldThrowExceptionWhenTicketNotFoundForModifySeat() {
        String email = "nonexistent@example.com";
        Seat requestedSeat = new Seat("B1", Section.B);

        when(ticketRepository.findByUserEmail(email)).thenReturn(null);

        TicketNotFoundException exception = assertThrows(TicketNotFoundException.class,
                () -> ticketService.modifySeat(email, requestedSeat));

        assertEquals("No ticket found for email: " + email, exception.getMessage());
    }

    @Test
    void shouldModifySeatSuccessfully() {
        String email = "john.doe@example.com";

        Ticket ticket = Ticket.builder()
                .id("1")
                .user(User.builder()
                        .email(email)
                        .build())
                .from("London")
                .to("France")
                .pricePaid(20.0)
                .seat(new Seat("A1", Section.A))
                .build();

        Seat requestedSeat = new Seat("B1", Section.B);

        when(ticketRepository.findByUserEmail(email)).thenReturn(ticket);
        when(seatAllocationService.isSeatAvailable(requestedSeat)).thenReturn(true);
        when(seatAllocationService.reallocateSeat(ticket.getSeat(), requestedSeat)).thenReturn(requestedSeat);

        Ticket updatedTicket = ticketService.modifySeat(email, requestedSeat);

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
                        .email("john.doe@example.com")
                        .build())
                .seat(new Seat("A1", Section.A))
                .build();

        Ticket ticket2 = Ticket.builder()
                .id("2")
                .user(User.builder()
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