package ru.job4j.persistence;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.Main;
import ru.job4j.models.Session;
import ru.job4j.models.Ticket;
import ru.job4j.models.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

public class TicketDBStoreTest {
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
    public void whenAddTicket() {
        TicketDBStore ticketStore = new TicketDBStore(pool);
        UsersDBStore userStore = new UsersDBStore(pool);
        SessionsDBStore sessionStore = new SessionsDBStore(pool);
        User user = new User("Ivan", "123@mail", "2-00-00");
        userStore.addUser(user);
        Session session = new Session("Taxi");
        sessionStore.addSession(session);
        Ticket ticket = new Ticket(session, user, 2, 2);
        ticketStore.add(ticket);
        Ticket ticketFromDB = ticketStore.findById(ticket.getId()).get();
        assertThat(ticketFromDB.getSession().getId()).isEqualTo(ticket.getSession().getId());
        assertThat(ticketFromDB.getUser().getId()).isEqualTo(ticket.getUser().getId());
        assertThat(ticketFromDB.getRow()).isEqualTo(ticket.getRow());
        assertThat(ticketFromDB.getCell()).isEqualTo(ticket.getCell());
    }

    @Test
    public void whenUpdateTicketThenMustBeChangedWithSameId() {
        TicketDBStore ticketStore = new TicketDBStore(pool);
        UsersDBStore userStore = new UsersDBStore(pool);
        SessionsDBStore sessionStore = new SessionsDBStore(pool);
        User user = new User("Ivan", "123@mail", "2-00-00");
        User changeUser = new User("Petr", "abc@gmail", "5-00-00");
        userStore.addUser(user);
        userStore.addUser(changeUser);
        Session session = new Session("Taxi");
        Session changeSession = new Session("Blade");
        sessionStore.addSession(session);
        sessionStore.addSession(changeSession);
        Ticket ticket = new Ticket(session, user, 2, 2);
        ticketStore.add(ticket);
        Ticket changedTicket = new Ticket(ticket.getId(), changeSession, changeUser, 4, 4);
        ticketStore.update(changedTicket);
        Ticket ticketFromDB = ticketStore.findById(ticket.getId()).get();
        assertThat(ticketFromDB.getSession().getId()).isEqualTo(changedTicket.getSession().getId());
        assertThat(ticketFromDB.getUser().getId()).isEqualTo(changedTicket.getUser().getId());
        assertThat(ticketFromDB.getRow()).isEqualTo(changedTicket.getRow());
        assertThat(ticketFromDB.getCell()).isEqualTo(changedTicket.getCell());
    }

    @Test
    public void whenAddTwoTicketsThenFindAllMustReturnsBoth() {
        TicketDBStore ticketStore = new TicketDBStore(pool);
        UsersDBStore userStore = new UsersDBStore(pool);
        SessionsDBStore sessionStore = new SessionsDBStore(pool);
        User user1 = new User("Ivan", "123@mail", "2-00-00");
        User user2 = new User("Petr", "abc@gmail", "5-00-00");
        userStore.addUser(user1);
        userStore.addUser(user2);
        Session session1 = new Session("Taxi");
        Session session2 = new Session("Blade");
        sessionStore.addSession(session1);
        sessionStore.addSession(session2);
        Ticket ticket1 = new Ticket(session1, user1, 2, 2);
        Ticket ticket2 = new Ticket(session2, user2, 4, 4);
        ticketStore.add(ticket1);
        ticketStore.add(ticket2);
        List<Ticket> expected = List.of(ticket1, ticket2);
        assertThat(ticketStore.findAll()).isEqualTo(expected);
    }

    @Test
    public void whenTicketIsNotIntoStoreThenFindByIdMustReturnsEmptyOptional() {
        TicketDBStore ticketStore = new TicketDBStore(pool);
        assertThat(ticketStore.findById(1)).isEqualTo(Optional.empty());
    }

    @Test
    public void whenAddTicketThenMustBeInstallIdIntoIt() {
        TicketDBStore ticketStore = new TicketDBStore(pool);
        UsersDBStore userStore = new UsersDBStore(pool);
        SessionsDBStore sessionStore = new SessionsDBStore(pool);
        User user = new User("Ivan", "123@mail", "2-00-00");
        Session session = new Session("Taxi");
        userStore.addUser(user);
        sessionStore.addSession(session);
        Ticket ticket = new Ticket(session, user, 2, 2);
        ticketStore.add(ticket);
        assertThat(ticket.getId()).isNotEqualTo(0);
    }
}