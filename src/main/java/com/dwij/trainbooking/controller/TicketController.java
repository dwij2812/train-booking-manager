package com.dwij.trainbooking.controller;

import com.dwij.trainbooking.models.Seat;
import com.dwij.trainbooking.models.Section;
import com.dwij.trainbooking.models.Ticket;
import com.dwij.trainbooking.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/purchase")
    public ResponseEntity<Ticket> purchaseTicket(@RequestParam String email, @RequestParam Section section) {
        Ticket ticket = ticketService.purchaseTicket(email, section);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/{email}/receipt")
    public ResponseEntity<Ticket> getReceipt(@PathVariable String email) {
        Ticket ticket = ticketService.getTicket(email);
        return ResponseEntity.ok(ticket);
    }

    @DeleteMapping("/{email}/remove")
    public ResponseEntity<Void> removeUserFromTrain(@PathVariable String email) {
        ticketService.cancelTicket(email);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{email}/modify-seat")
    public ResponseEntity<Ticket> modifySeat(@PathVariable String email, @RequestBody Seat requestedSeat) {
        Ticket ticket = ticketService.modifySeat(email, requestedSeat);
        return ResponseEntity.ok(ticket);
    }
}