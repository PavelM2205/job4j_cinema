package ru.job4j.service;

import org.springframework.stereotype.Service;
import ru.job4j.exception.TicketWithSuchSessionAndPlaceAlreadyExists;
import ru.job4j.model.Ticket;
import ru.job4j.repository.TicketRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TicketService {
    private final TicketRepository ticketStore;

    public TicketService(TicketRepository store) {
        this.ticketStore = store;
    }

    public Ticket add(Ticket ticket) {
        Optional<Ticket> optTicket = ticketStore.add(ticket);
        if (optTicket.isEmpty()) {
            var exc = new TicketWithSuchSessionAndPlaceAlreadyExists("Ticket was not added");
            exc.setTicket(ticket);
            throw exc;
        }
        return optTicket.get();
    }

    public Ticket findById(int id) {
        Optional<Ticket> optTicket = ticketStore.findById(id);
        if (optTicket.isEmpty()) {
            throw new IllegalArgumentException("Ticket with such id does not exists");
        }
        return optTicket.get();
    }

    public boolean update(Ticket ticket) {
        return ticketStore.update(ticket);
    }

    public List<Ticket> findAll() {
        return ticketStore.findAll();
    }
}
