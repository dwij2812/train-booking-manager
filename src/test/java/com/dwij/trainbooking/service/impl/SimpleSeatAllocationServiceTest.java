package com.dwij.trainbooking.service.impl;

import com.dwij.trainbooking.exception.SeatUnavailableException;
import com.dwij.trainbooking.models.Seat;
import com.dwij.trainbooking.models.Section;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

class SimpleSeatAllocationServiceTest {

    private SimpleSeatAllocationService seatService;

    @BeforeEach
    void setUp() {
        seatService = new SimpleSeatAllocationService();
    }

    @Test
    void shouldAllocateSeatFromSection() {
        Seat seat = seatService.allocateSeat(Section.A);
        assertThat(seat).isNotNull();
        assertThat(seat.getSection()).isEqualTo(Section.A);
        assertThat(seat.getSeatNumber()).startsWith("A");
    }

    @Test
    void shouldReleaseSeatBackToSection() {
        Seat seat = seatService.allocateSeat(Section.B);
        seatService.releaseSeat(seat);

        Seat reallocated = seatService.allocateSeat(Section.B);
        assertThat(reallocated.getSeatNumber()).isEqualTo(seat.getSeatNumber());
    }

    @Test
    void shouldThrowExceptionWhenNoSeatsAvailable() {
        for (int i = 0; i < 10; i++) {
            seatService.allocateSeat(Section.A);
        }

        assertThatThrownBy(() -> seatService.allocateSeat(Section.A))
                .isInstanceOf(SeatUnavailableException.class)
                .hasMessageContaining("No seats available");
    }

    @Test
    void shouldReturnAllAvailableSeatsForSection() {
        Set<String> expectedSeatNumbers = new HashSet<>();
        for (int i = 1; i <= 10; i++) {
            expectedSeatNumbers.add("B" + i);
        }

        Set<String> actual = new HashSet<>();
        seatService.getAvailableSeats(Section.B).forEach(seat -> actual.add(seat.getSeatNumber()));

        assertThat(actual).hasSize(10);
        assertThat(actual).isEqualTo(expectedSeatNumbers);
    }
}
