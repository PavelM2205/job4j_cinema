package ru.job4j.service;

import ru.job4j.models.Ticket;
import ru.job4j.persistence.TicketDBStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TicketService {
    private final TicketDBStore store;
    private final SessionService sessionService;
    private final UserService userService;

    public TicketService(TicketDBStore store, SessionService sessionService,
                         UserService userService) {
        this.store = store;
        this.sessionService = sessionService;
        this.userService = userService;
    }

    public Ticket add(Ticket ticket) {
        return store.add(ticket);
    }

    public Optional<Ticket> findById(int id) {
        Optional<Ticket> optionalTicket = store.findById(id);
        if (optionalTicket.isPresent()) {
            Ticket ticket = optionalTicket.get();
            optionalTicket = Optional.of(loadUserAndSessionIntoTicketById(ticket));
        }
        return optionalTicket;
    }

    public boolean update(Ticket ticket) {
        return store.update(ticket);
    }

    public List<Ticket> findAll() {
        List<Ticket> result = new ArrayList<>(store.findAll());
        return result.stream()
                .map(this::loadUserAndSessionIntoTicketById)
                .toList();
    }

    private Ticket loadUserAndSessionIntoTicketById(Ticket ticket) {
        ticket.setSession(sessionService.findById(ticket.getSession().getId()).get());
        ticket.setUser(userService.findById(ticket.getUser().getId()).get());
        return ticket;
    }
}
