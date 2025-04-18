package com.dwij.trainbooking.service;

import com.dwij.trainbooking.models.Seat;
import com.dwij.trainbooking.models.Section;

import java.util.List;

public interface SeatAllocationStrategy {
    Seat allocateSeat(Section section);
    Seat reallocateSeat(Seat currentSeat, Seat newSeat);
    void releaseSeat(Seat seat);
    List<Seat> getAvailableSeats(Section section);
}


