package ru.job4j.service;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.Main;
import ru.job4j.exception.TicketWithSuchSessionAndPlaceAlreadyExists;
import ru.job4j.model.Ticket;
import ru.job4j.repository.TicketRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TicketServiceTest {
    private static BasicDataSource pool;

    @BeforeAll
    public static void getPool() {
        pool = new Main().loadPool();
    }

    @Test
    public void whenAddTicketReturnsOptionalEmptyThenMustBeException() {
        TicketRepository ticketStore = mock(TicketRepository.class);
        TicketService service = new TicketService(ticketStore);
        Ticket ticket = mock(Ticket.class);
        when(ticketStore.add(ticket)).thenReturn(Optional.empty());
        assertThrows(TicketWithSuchSessionAndPlaceAlreadyExists.class,
                () -> service.add(ticket));
    }

    @Test
    public void whenFindByIdReturnsOptionalEmptyThenMustBeException() {
        TicketRepository ticketStore = new TicketRepository(pool);
        TicketService service = new TicketService(ticketStore);
        assertThrows(IllegalArgumentException.class, () -> service.findById(0));
    }
}