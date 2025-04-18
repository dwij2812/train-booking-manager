package com.dwij.trainbooking.models;

public class Ticket {
    private String from;
    private String to;
    private User user;
    private double pricePaid;
    private Seat seat;

    private Ticket(Builder builder) {
        this.from = builder.from;
        this.to = builder.to;
        this.user = builder.user;
        this.pricePaid = builder.pricePaid;
        this.seat = builder.seat;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String from;
        private String to;
        private User user;
        private double pricePaid;
        private Seat seat;

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder to(String to) {
            this.to = to;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder pricePaid(double pricePaid) {
            this.pricePaid = pricePaid;
            return this;
        }

        public Builder seat(Seat seat) {
            this.seat = seat;
            return this;
        }

        public Ticket build() {
            return new Ticket(this);
        }
    }

    public String getFrom() { return from; }
    public String getTo() { return to; }
    public User getUser() { return user; }
    public double getPricePaid() { return pricePaid; }
    public Seat getSeat() { return seat; }

    @Override
    public String toString() {
        return "Ticket[from=" + from + ", to=" + to + ", user=" + user + ", pricePaid=$" + pricePaid + ", seat=" + seat + "]";
    }
}
