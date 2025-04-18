package com.dwij.trainbooking.models;

public class Seat {
    private String seatNumber;
    private Section section;

    private Seat(Builder builder) {
        this.seatNumber = builder.seatNumber;
        this.section = builder.section;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String seatNumber;
        private Section section;

        public Builder seatNumber(String seatNumber) {
            this.seatNumber = seatNumber;
            return this;
        }

        public Builder section(Section section) {
            this.section = section;
            return this;
        }

        public Seat build() {
            return new Seat(this);
        }
    }

    public String getSeatNumber() { return seatNumber; }
    public Section getSection() { return section; }

    @Override
    public String toString() {
        return seatNumber + " (Section " + section + ")";
    }
}
