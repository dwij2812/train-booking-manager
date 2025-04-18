package com.dwij.trainbooking.models;

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
}
