package com.dwij.trainbooking.service.impl;

import com.dwij.trainbooking.exception.TicketAlreadyExistsException;
import com.dwij.trainbooking.exception.TicketNotFoundException;
import com.dwij.trainbooking.models.*;
import com.dwij.trainbooking.repository.TicketRepository;
import com.dwij.trainbooking.service.TicketService;
import com.dwij.trainbooking.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;
    private final SimpleSeatAllocationService seatAllocationService;
    private final UserService userService;

    public TicketServiceImpl(TicketRepository ticketRepository, SimpleSeatAllocationService seatAllocationService,
            UserService userService) {
        this.ticketRepository = ticketRepository;
        this.seatAllocationService = seatAllocationService;
        this.userService = userService;
    }

    @Override
    public Ticket purchaseTicket(String email, Section section) {
        User user = userService.getUserByEmail(email);

        Ticket existingTicket = ticketRepository.findByUserEmail(email);

        if (existingTicket != null) {
            throw new TicketAlreadyExistsException("A ticket is already booked for this email: " + email);
        }

        Seat seat = seatAllocationService.allocateSeat(section);

        Ticket ticket = Ticket.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .from("London")
                .to("France")
                .pricePaid(20.0)
                .seat(seat)
                .build();

        ticketRepository.save(ticket);
        return ticket;
    }

    @Override
    public Ticket getTicket(String email) {
        Ticket ticket = ticketRepository.findByUserEmail(email);
        if (ticket == null) {
            throw new TicketNotFoundException("No ticket found for email: " + email);
        }
        return ticket;
    }

    @Override
    public void cancelTicket(String email) {
        Ticket ticket = ticketRepository.findByUserEmail(email);
        if (ticket == null) {
            throw new TicketNotFoundException("No ticket found for email: " + email);
        }
        seatAllocationService.releaseSeat(ticket.getSeat());
        ticketRepository.deleteByUserEmail(email);
    }

    @Override
    public Ticket modifySeat(String email, Seat requestedSeat) {
        Ticket ticket = getTicket(email);
        Seat newSeat = seatAllocationService.reallocateSeat(ticket.getSeat(), requestedSeat);
        Ticket updatedTicket = ticket.withSeat(newSeat);
        ticketRepository.save(updatedTicket);
        return updatedTicket;
    }

    @Override
    public List<String> getUsersAndSeatsBySection(Section section) {
        return ticketRepository.findAll().values().stream()
                .filter(ticket -> ticket.getSeat().getSection() == section)
                .sorted((t1, t2) -> {
                    int seatNumber1 = Integer.parseInt(t1.getSeat().getSeatNumber().replaceAll("\\D", ""));
                    int seatNumber2 = Integer.parseInt(t2.getSeat().getSeatNumber().replaceAll("\\D", ""));
                    return Integer.compare(seatNumber1, seatNumber2);
                })
                .map(ticket -> "User: " + ticket.getUser().getEmail() + ", Seat: " + ticket.getSeat().getSeatNumber())
                .collect(Collectors.toList());
    }
}