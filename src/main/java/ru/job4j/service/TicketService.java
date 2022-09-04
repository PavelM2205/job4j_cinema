package ru.job4j.service;

import org.springframework.stereotype.Service;
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

    public Optional<Ticket> add(Ticket ticket) {
        return ticketStore.add(ticket);
    }

    public Optional<Ticket> findById(int id) {
        return ticketStore.findById(id);
    }

    public boolean update(Ticket ticket) {
        return ticketStore.update(ticket);
    }

    public List<Ticket> findAll() {
        return ticketStore.findAll();
    }
}
