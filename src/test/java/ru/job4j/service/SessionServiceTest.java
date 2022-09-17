package ru.job4j.service;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.Main;
import ru.job4j.model.Session;
import ru.job4j.repository.SessionsRepository;
import ru.job4j.repository.TicketRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SessionServiceTest {
    private static BasicDataSource pool;

    @BeforeAll
    public static void getPool() {
        pool = new Main().loadPool();
    }

    @Test
    public void whenAddSessionReturnsOptionalEmptyThenMustBeException() {
        SessionsRepository sessionStore = mock(SessionsRepository.class);
        Session session = mock(Session.class);
        TicketRepository ticketStore = new TicketRepository(pool);
        SessionService service = new SessionService(sessionStore, ticketStore);
        when(sessionStore.addSession(session)).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> service.add(session));
    }

    @Test
    public void whenFindByIdReturnsOptionalEmptyThenMustBeException() {
        SessionsRepository sessionStore = new SessionsRepository(pool);
        TicketRepository ticketStore = new TicketRepository(pool);
        SessionService service = new SessionService(sessionStore, ticketStore);
        assertThrows(IllegalArgumentException.class, () -> service.findById(0));
    }
}