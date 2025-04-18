package com.dwij.trainbooking.models;

public class Ticket {
    private final String id;
    private final User user;
    private final String from;
    private final String to;
    private final double pricePaid;
    private final Seat seat;

    private Ticket(Builder builder) {
        this.id = builder.id;
        this.user = builder.user;
        this.from = builder.from;
        this.to = builder.to;
        this.pricePaid = builder.pricePaid;
        this.seat = builder.seat;
    }

    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public double getPricePaid() {
        return pricePaid;
    }

    public Seat getSeat() {
        return seat;
    }

    public Ticket withSeat(Seat newSeat) {
        return Ticket.builder()
                .id(this.id)
                .user(this.user)
                .from(this.from)
                .to(this.to)
                .pricePaid(this.pricePaid)
                .seat(newSeat)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private User user;
        private String from;
        private String to;
        private double pricePaid;
        private Seat seat;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder to(String to) {
            this.to = to;
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

    @Override
    public String toString() {
        return "Ticket{" +
                "id='" + id + '\'' +
                ", user=" + user +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", pricePaid=" + pricePaid +
                ", seat=" + seat +
                '}';
    }
}
