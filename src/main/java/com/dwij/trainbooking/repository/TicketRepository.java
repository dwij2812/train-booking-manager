package com.dwij.trainbooking.repository;

import com.dwij.trainbooking.exception.UserAlreadyExistsException;
import com.dwij.trainbooking.models.Ticket;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class TicketRepository {
    private final Map<String, Ticket> tickets = new HashMap<>();

    public void save(Ticket ticket) {
        if (tickets.containsKey(ticket.getUser().getEmail())) {
            throw new UserAlreadyExistsException("A ticket is already booked for this email: " + ticket.getUser().getEmail());
        }
        tickets.put(ticket.getUser().getEmail(), ticket);
    }

    public Ticket findByUserEmail(String email) {
        return tickets.get(email);
    }

    public void deleteByUserEmail(String email) {
        tickets.remove(email);
    }

    public Map<String, Ticket> findAll() {
        return tickets;
    }
}
