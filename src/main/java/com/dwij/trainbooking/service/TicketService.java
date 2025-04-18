package com.dwij.trainbooking.service;

import com.dwij.trainbooking.models.Seat;
import com.dwij.trainbooking.models.Section;
import com.dwij.trainbooking.models.Ticket;

import java.util.List;

public interface TicketService {

    Ticket purchaseTicket(String email, Section section);

    Ticket getTicket(String email);

    void cancelTicket(String email);

    Ticket modifySeat(String email, Seat requestedSeat);

    List<String> getUsersAndSeatsBySection(Section section);
    
}