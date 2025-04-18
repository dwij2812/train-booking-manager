package com.dwij.trainbooking.controller;

import com.dwij.trainbooking.models.Seat;
import com.dwij.trainbooking.models.Section;
import com.dwij.trainbooking.service.SeatAllocationStrategy;
import com.dwij.trainbooking.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    private final SeatAllocationStrategy seatAllocationService;
    private final TicketService ticketService;

    @Autowired
    public SeatController(SeatAllocationStrategy seatAllocationService, TicketService ticketService) {
        this.seatAllocationService = seatAllocationService;
        this.ticketService = ticketService;
    }

    @GetMapping("/available/{section}")
    public ResponseEntity<List<Seat>> getAvailableSeats(@PathVariable Section section) {
        List<Seat> availableSeats = seatAllocationService.getAvailableSeats(section);
        return ResponseEntity.ok(availableSeats);
    }

    @GetMapping("/allocated/{section}")
    public ResponseEntity<List<String>> getUsersAndSeatsBySection(@PathVariable Section section) {
        List<String> usersAndSeats = ticketService.getUsersAndSeatsBySection(section);
        return ResponseEntity.ok(usersAndSeats);
    }
}