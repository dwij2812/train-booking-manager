package com.dwij.trainbooking.models;

public class TicketRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String from;
    private String to;
    private Section section;

    public TicketRequest() {}

    public TicketRequest(String firstName, String lastName, String email, String from, String to, Section section) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.from = from;
        this.to = to;
        this.section = section;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    @Override
    public String toString() {
        return "TicketRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", section=" + section +
                '}';
    }
}