package com.dwij.trainbooking.service.impl;

import com.dwij.trainbooking.exception.SeatUnavailableException;
import com.dwij.trainbooking.exception.TicketAlreadyExistsException;
import com.dwij.trainbooking.exception.UserNotFoundException;
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

    public TicketServiceImpl(TicketRepository ticketRepository, SimpleSeatAllocationService seatAllocationService, UserService userService) {
        this.ticketRepository = ticketRepository;
        this.seatAllocationService = seatAllocationService;
        this.userService = userService;
    }

    @Override
    public Ticket purchaseTicket(TicketRequest request) {
        if (ticketRepository.findByUserEmail(request.getEmail()) != null) {
            throw new TicketAlreadyExistsException("A ticket is already booked for this email: " + request.getEmail());
        }

        User user;
        try {
            user = userService.getUserByEmail(request.getEmail());
        } catch (UserNotFoundException e) {
            user = User.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .build();
            userService.addUser(user);
        }

        Seat seat = seatAllocationService.allocateSeat(request.getSection());
        if (seat == null) {
            throw new SeatUnavailableException("No seats available in section: " + request.getSection());
        }

        Ticket ticket = Ticket.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .from(request.getFrom())
                .to(request.getTo())
                .pricePaid(20.0)
                .seat(seat)
                .build();

        ticketRepository.save(ticket);
        return ticket;
    }

    @Override
    public Ticket getTicket(String email) {
        return ticketRepository.findByUserEmail(email);
    }

    @Override
    public void cancelTicket(String email) {
        Ticket ticket = ticketRepository.findByUserEmail(email);
        if (ticket != null) {
            seatAllocationService.releaseSeat(ticket.getSeat());
            ticketRepository.deleteByUserEmail(email);
        }
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