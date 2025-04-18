package com.dwij.trainbooking.service.impl;

import com.dwij.trainbooking.exception.SeatUnavailableException;
import com.dwij.trainbooking.models.Seat;
import com.dwij.trainbooking.models.Section;
import com.dwij.trainbooking.service.SeatAllocationStrategy;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SimpleSeatAllocationService implements SeatAllocationStrategy {
    private static final int MAX_SEATS_PER_SECTION = 10;
    private final Map<Section, Queue<Seat>> availableSeats = new EnumMap<>(Section.class);

    public SimpleSeatAllocationService() {
        for (Section section : Section.values()) {
            PriorityQueue<Seat> seats = new PriorityQueue<>(Comparator.comparingInt(seat -> 
                Integer.parseInt(seat.getSeatNumber().replaceAll("\\D", ""))));
            for (int i = 1; i <= MAX_SEATS_PER_SECTION; i++) {
                seats.add(new Seat(section.name() + i, section));
            }
            availableSeats.put(section, seats);
        }
    }

    @Override
    public Seat allocateSeat(Section section) {
        Seat seat = availableSeats.get(section).poll();
        if (seat == null) {
            throw new SeatUnavailableException("No seats available in section: " + section);
        }
        return seat;
    }

    @Override
    public Seat reallocateSeat(Seat currentSeat, Seat requestedSeat) {
        if (currentSeat.equals(requestedSeat)) {
                    return currentSeat;
        }

        if (!isSeatAvailable(requestedSeat)) {
            throw new SeatUnavailableException("The requested seat " + requestedSeat.getSeatNumber() + " is not available.");
        }
    
        releaseSeat(currentSeat);
        availableSeats.get(requestedSeat.getSection()).remove(requestedSeat);
        return requestedSeat;
    }

    public boolean isSeatAvailable(Seat seat) {
        return availableSeats.get(seat.getSection()).contains(seat);
    }

    @Override
    public void releaseSeat(Seat seat) {
        availableSeats.get(seat.getSection()).offer(seat);
    }

    @Override
    public List<Seat> getAvailableSeats(Section section) {
        return new ArrayList<>(availableSeats.get(section));
    }
}
