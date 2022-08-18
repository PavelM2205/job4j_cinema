package ru.job4j.service;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.Main;
import ru.job4j.models.Session;
import ru.job4j.models.Ticket;
import ru.job4j.models.User;
import ru.job4j.persistence.SessionsDBStore;
import ru.job4j.persistence.TicketDBStore;
import ru.job4j.persistence.UsersDBStore;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class TicketServiceTest {
    private static BasicDataSource pool;

    @BeforeAll
    public static void loadPool() {
        pool = new Main().loadPool();
    }

    @AfterEach
    public void cleanTables() throws SQLException {
        try (PreparedStatement st = pool.getConnection().prepareStatement(
                "DELETE FROM ticket")) {
            st.execute();
        }
        try (PreparedStatement st = pool.getConnection().prepareStatement(
                "DELETE FROM users"
        )) {
            st.execute();
        }
        try (PreparedStatement st = pool.getConnection().prepareStatement(
                "DELETE FROM sessions")) {
            st.execute();
        }
    }

    @Test
    public void whenFindByIdThenUserAndSessionMustBeLoadedIntoTicket() {
        TicketDBStore ticketStore = new TicketDBStore(pool);
        UsersDBStore userStore = new UsersDBStore(pool);
        SessionsDBStore sessionStore = new SessionsDBStore(pool);
        SessionService sessionService = new SessionService(sessionStore);
        UserService userService = new UserService(userStore);
        TicketService ticketService = new TicketService(
                ticketStore, sessionService, userService
        );
        User user = new User("Ivan", "123@mail", "2-00-00");
        Session session = new Session("Blade");
        userStore.addUser(user);
        sessionStore.addSession(session);
        Ticket ticket = new Ticket(session, user, 2, 2);
        ticketStore.add(ticket);
        Ticket ticketFromDB = ticketStore.findById(ticket.getId()).get();
        Ticket ticketFromService = ticketService.findById(ticket.getId()).get();
        assertThat(ticketFromDB.getUser().getId()).isNotEqualTo(0);
        assertThat(ticketFromDB.getUser().getUsername()).isNull();
        assertThat(ticketFromDB.getUser().getEmail()).isNull();
        assertThat(ticketFromDB.getUser().getPhone()).isNull();
        assertThat(ticketFromDB.getSession().getId()).isNotEqualTo(0);
        assertThat(ticketFromDB.getSession().getName()).isNull();
        assertThat(ticketFromService.getUser().getUsername()).isEqualTo(user.getUsername());
        assertThat(ticketFromService.getUser().getEmail()).isEqualTo(user.getEmail());
        assertThat(ticketFromService.getUser().getPhone()).isEqualTo(user.getPhone());
        assertThat(ticketFromService.getSession().getName()).isEqualTo(session.getName());
    }

    @Test
    public void whenFindAllThenUserAndSessionMustBeLoadedIntoAllTickets() {
            TicketDBStore ticketStore = new TicketDBStore(pool);
            UsersDBStore userStore = new UsersDBStore(pool);
            SessionsDBStore sessionStore = new SessionsDBStore(pool);
            SessionService sessionService = new SessionService(sessionStore);
            UserService userService = new UserService(userStore);
            TicketService ticketService = new TicketService(
                    ticketStore, sessionService, userService);
            User user1 = new User("Ivan", "123@mail", "2-00-00");
            User user2 = new User("Pavel", "abc@mail", "5-00-00");
            Session session1 = new Session("Blade");
            Session session2 = new Session("Taxi");
            userStore.addUser(user1);
            userStore.addUser(user2);
            sessionStore.addSession(session1);
            sessionStore.addSession(session2);
            Ticket ticket1 = new Ticket(session1, user1, 2, 2);
            Ticket ticket2 = new Ticket(session2, user2, 4, 4);
            ticketStore.add(ticket1);
            ticketStore.add(ticket2);
            List<Ticket> ticketsFromDB = ticketStore.findAll();
            List<Ticket> ticketsFromService = ticketService.findAll();
            assertThat(ticketsFromDB.get(0).getUser().getEmail()).isNull();
            assertThat(ticketsFromDB.get(0).getUser().getPhone()).isNull();
            assertThat(ticketsFromDB.get(0).getSession().getName()).isNull();
            assertThat(ticketsFromDB.get(1).getUser().getUsername()).isNull();
            assertThat(ticketsFromDB.get(1).getUser().getEmail()).isNull();
            assertThat(ticketsFromDB.get(1).getUser().getPhone()).isNull();
            assertThat(ticketsFromDB.get(1).getSession().getName()).isNull();
            assertThat(ticketsFromService.get(0).getUser().getUsername())
                    .isEqualTo(user1.getUsername());
            assertThat(ticketsFromService.get(0).getUser().getEmail())
                    .isEqualTo(user1.getEmail());
            assertThat(ticketsFromService.get(0).getUser().getPhone())
                    .isEqualTo(user1.getPhone());
            assertThat(ticketsFromService.get(0).getSession().getName())
                    .isEqualTo(session1.getName());
            assertThat(ticketsFromService.get(1).getUser().getUsername())
                    .isEqualTo(user2.getUsername());
            assertThat(ticketsFromService.get(1).getUser().getEmail())
                    .isEqualTo(user2.getEmail());
            assertThat(ticketsFromService.get(1).getUser().getPhone())
                    .isEqualTo(user2.getPhone());
            assertThat(ticketsFromService.get(1).getSession().getName())
                    .isEqualTo(session2.getName());
    }
}