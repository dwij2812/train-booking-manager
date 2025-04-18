package com.dwij.trainbooking.models;

import java.util.Objects;

public class Seat {
    private final String seatNumber;
    private final Section section;

    public Seat(String seatNumber, Section section) {
        this.seatNumber = seatNumber;
        this.section = section;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public Section getSection() {
        return section;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "seatNumber='" + seatNumber + '\'' +
                ", section=" + section +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seat seat = (Seat) o;
        return seatNumber.equals(seat.seatNumber) && section == seat.section;
    }

    @Override
    public int hashCode() {
        return Objects.hash(seatNumber, section);
    }
}
