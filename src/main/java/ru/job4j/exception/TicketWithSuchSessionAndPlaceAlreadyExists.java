package ru.job4j.exception;

import ru.job4j.model.Ticket;

public class TicketWithSuchSessionAndPlaceAlreadyExists extends RuntimeException {
    private Ticket ticket;

    public TicketWithSuchSessionAndPlaceAlreadyExists(String message) {
        super(message);
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
}
