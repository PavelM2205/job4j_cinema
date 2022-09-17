package ru.job4j.repository;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.Main;
import ru.job4j.model.Place;
import ru.job4j.model.Session;
import ru.job4j.model.Ticket;
import ru.job4j.model.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

public class TicketRepositoryTest {
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
        TicketRepository ticketStore = new TicketRepository(pool);
        UsersRepository userStore = new UsersRepository(pool);
        SessionsRepository sessionStore = new SessionsRepository(pool);
        PlaceRepository placeStore = new PlaceRepository(pool);
        User user = new User("Ivan", "123@mail", "2-00-00");
        userStore.addUser(user);
        Session session = new Session("Taxi");
        sessionStore.addSession(session);
        Place place = placeStore.findById(2).get();
        Ticket ticket = new Ticket(session, user, place);
        ticketStore.add(ticket);
        Ticket ticketFromDB = ticketStore.findById(ticket.getId()).get();
        assertThat(ticketFromDB.getSession().getId()).isEqualTo(ticket.getSession().getId());
        assertThat(ticketFromDB.getUser().getId()).isEqualTo(ticket.getUser().getId());
        assertThat(ticketFromDB.getPlace().getId()).isEqualTo(ticket.getPlace().getId());
    }

    @Test
    public void whenUpdateTicketThenMustBeChangedWithSameId() {
        TicketRepository ticketStore = new TicketRepository(pool);
        UsersRepository userStore = new UsersRepository(pool);
        SessionsRepository sessionStore = new SessionsRepository(pool);
        PlaceRepository placeStore = new PlaceRepository(pool);
        User user = new User("Ivan", "123@mail", "2-00-00");
        User changeUser = new User("Petr", "abc@gmail", "5-00-00");
        userStore.addUser(user);
        userStore.addUser(changeUser);
        Session session = new Session("Taxi");
        Session changeSession = new Session("Blade");
        sessionStore.addSession(session);
        sessionStore.addSession(changeSession);
        Place place1 = placeStore.findById(2).get();
        Place place2 = placeStore.findById(3).get();
        Ticket ticket = new Ticket(session, user, place1);
        ticketStore.add(ticket);
        Ticket changedTicket = new Ticket(ticket.getId(), changeSession, changeUser, place2);
        ticketStore.update(changedTicket);
        Ticket ticketFromDB = ticketStore.findById(ticket.getId()).get();
        assertThat(ticketFromDB.getSession().getId()).isEqualTo(changedTicket.getSession().getId());
        assertThat(ticketFromDB.getUser().getId()).isEqualTo(changedTicket.getUser().getId());
        assertThat(ticketFromDB.getPlace().getId()).isEqualTo(changedTicket.getPlace().getId());
    }

    @Test
    public void whenAddTwoTicketsThenFindAllMustReturnsBoth() {
        TicketRepository ticketStore = new TicketRepository(pool);
        UsersRepository userStore = new UsersRepository(pool);
        SessionsRepository sessionStore = new SessionsRepository(pool);
        PlaceRepository placeStore = new PlaceRepository(pool);
        User user1 = new User("Ivan", "123@mail", "2-00-00");
        User user2 = new User("Petr", "abc@gmail", "5-00-00");
        userStore.addUser(user1);
        userStore.addUser(user2);
        Session session1 = new Session("Taxi");
        Session session2 = new Session("Blade");
        sessionStore.addSession(session1);
        sessionStore.addSession(session2);
        Place place1 = placeStore.findById(2).get();
        Place place2 = placeStore.findById(3).get();
        Ticket ticket1 = new Ticket(session1, user1, place1);
        Ticket ticket2 = new Ticket(session2, user2, place2);
        ticketStore.add(ticket1);
        ticketStore.add(ticket2);
        List<Ticket> expected = List.of(ticket1, ticket2);
        assertThat(ticketStore.findAll()).isEqualTo(expected);
    }

    @Test
    public void whenTicketIsNotIntoStoreThenFindByIdMustReturnsEmptyOptional() {
        TicketRepository ticketStore = new TicketRepository(pool);
        assertThat(ticketStore.findById(1)).isEqualTo(Optional.empty());
    }

    @Test
    public void whenAddTicketThenMustBeInstallIdIntoIt() {
        TicketRepository ticketStore = new TicketRepository(pool);
        UsersRepository userStore = new UsersRepository(pool);
        SessionsRepository sessionStore = new SessionsRepository(pool);
        PlaceRepository placeStore = new PlaceRepository(pool);
        User user = new User("Ivan", "123@mail", "2-00-00");
        Session session = new Session("Taxi");
        userStore.addUser(user);
        sessionStore.addSession(session);
        Place place = placeStore.findById(2).get();
        Ticket ticket = new Ticket(session, user, place);
        ticketStore.add(ticket);
        assertThat(ticket.getId()).isNotEqualTo(0);
    }

    @Test
    public void whenFindByIdThenReturnsTicketWithFullUserSessionAndPlace() {
        TicketRepository ticketStore = new TicketRepository(pool);
        UsersRepository userStore = new UsersRepository(pool);
        SessionsRepository sessionStore = new SessionsRepository(pool);
        PlaceRepository placeStore = new PlaceRepository(pool);
        User user = new User("Ivan", "123@mail", "2-00-00");
        userStore.addUser(user);
        Session session = new Session("Taxi");
        sessionStore.addSession(session);
        Place place = placeStore.findById(2).get();
        Ticket ticket = new Ticket(session, user, place);
        ticketStore.add(ticket);
        Ticket ticketFromDB = ticketStore.findById(ticket.getId()).get();
        assertThat(ticketFromDB.getSession().getId()).isEqualTo(session.getId());
        assertThat(ticketFromDB.getSession().getName()).isEqualTo(session.getName());
        assertThat(ticketFromDB.getUser().getId()).isEqualTo(user.getId());
        assertThat(ticketFromDB.getUser().getUsername()).isEqualTo(user.getUsername());
        assertThat(ticketFromDB.getUser().getEmail()).isEqualTo(user.getEmail());
        assertThat(ticketFromDB.getUser().getPhone()).isEqualTo(user.getPhone());
        assertThat(ticketFromDB.getPlace().getId()).isEqualTo(place.getId());
        assertThat(ticketFromDB.getPlace().getRow()).isEqualTo(place.getRow());
        assertThat(ticketFromDB.getPlace().getCell()).isEqualTo(place.getCell());
    }

    @Test
    public void whenFindAllThenReturnsTicketWithFullUserSessionAndPlace() {
        TicketRepository ticketStore = new TicketRepository(pool);
        UsersRepository userStore = new UsersRepository(pool);
        SessionsRepository sessionStore = new SessionsRepository(pool);
        PlaceRepository placeStore = new PlaceRepository(pool);
        User user1 = new User("Ivan", "123@mail", "2-00-00");
        User user2 = new User("Petr", "456@mail", "3-00-00");
        userStore.addUser(user1);
        userStore.addUser(user2);
        Session session1 = new Session("Taxi");
        Session session2 = new Session("Blade");
        sessionStore.addSession(session1);
        sessionStore.addSession(session2);
        Place place1 = placeStore.findById(2).get();
        Place place2 = placeStore.findById(3).get();
        Ticket ticket1 = new Ticket(session1, user1, place1);
        Ticket ticket2 = new Ticket(session2, user2, place2);
        ticketStore.add(ticket1);
        ticketStore.add(ticket2);
        List<Ticket> expected = List.of(ticket1, ticket2);
        List<Ticket> ticketsFromDB = ticketStore.findAll();
        assertThat(ticketsFromDB.get(0).getSession().getId()).isEqualTo(session1.getId());
        assertThat(ticketsFromDB.get(0).getSession().getName()).isEqualTo(session1.getName());
        assertThat(ticketsFromDB.get(0).getUser().getId()).isEqualTo(user1.getId());
        assertThat(ticketsFromDB.get(0).getUser().getUsername()).isEqualTo(user1.getUsername());
        assertThat(ticketsFromDB.get(0).getUser().getEmail()).isEqualTo(user1.getEmail());
        assertThat(ticketsFromDB.get(0).getUser().getPhone()).isEqualTo(user1.getPhone());
        assertThat(ticketsFromDB.get(0).getPlace().getId()).isEqualTo(place1.getId());
        assertThat(ticketsFromDB.get(0).getPlace().getRow()).isEqualTo(place1.getRow());
        assertThat(ticketsFromDB.get(0).getPlace().getCell()).isEqualTo(place1.getCell());
        assertThat(ticketsFromDB.get(1).getSession().getId()).isEqualTo(session2.getId());
        assertThat(ticketsFromDB.get(1).getSession().getName()).isEqualTo(session2.getName());
        assertThat(ticketsFromDB.get(1).getUser().getId()).isEqualTo(user2.getId());
        assertThat(ticketsFromDB.get(1).getUser().getUsername()).isEqualTo(user2.getUsername());
        assertThat(ticketsFromDB.get(1).getUser().getEmail()).isEqualTo(user2.getEmail());
        assertThat(ticketsFromDB.get(1).getUser().getPhone()).isEqualTo(user2.getPhone());
        assertThat(ticketsFromDB.get(1).getPlace().getId()).isEqualTo(place2.getId());
        assertThat(ticketsFromDB.get(1).getPlace().getRow()).isEqualTo(place2.getRow());
        assertThat(ticketsFromDB.get(1).getPlace().getCell()).isEqualTo(place2.getCell());
    }

    @Test
    public void whenFindAllTicketsForSomeSession() {
        TicketRepository ticketStore = new TicketRepository(pool);
        UsersRepository userStore = new UsersRepository(pool);
        SessionsRepository sessionStore = new SessionsRepository(pool);
        PlaceRepository placeStore = new PlaceRepository(pool);
        User user1 = new User("Ivan", "123@mail", "2-00-00");
        User user2 = new User("Petr", "456@mail", "3-00-00");
        userStore.addUser(user1);
        userStore.addUser(user2);
        Session session1 = new Session("Taxi");
        Session session2 = new Session("Blade");
        sessionStore.addSession(session1);
        sessionStore.addSession(session2);
        Place place1 = placeStore.findById(2).get();
        Place place2 = placeStore.findById(3).get();
        Ticket ticket1 = new Ticket(session1, user1, place1);
        Ticket ticket2 = new Ticket(session2, user2, place2);
        ticketStore.add(ticket1);
        ticketStore.add(ticket2);
        Ticket ticketForSessionFromDB = ticketStore.findAllTicketsForSomeSession(
                session2.getId()).get(0);
        assertThat(ticketForSessionFromDB.getId()).isEqualTo(ticket2.getId());
        assertThat(ticketForSessionFromDB.getUser().getId()).isEqualTo(user2.getId());
        assertThat(ticketForSessionFromDB.getUser().getUsername())
                .isEqualTo(user2.getUsername());
        assertThat(ticketForSessionFromDB.getUser().getEmail())
                .isEqualTo(user2.getEmail());
        assertThat(ticketForSessionFromDB.getUser().getPhone())
                .isEqualTo(user2.getPhone());
        assertThat(ticketForSessionFromDB.getSession().getId()).isEqualTo(session2.getId());
        assertThat(ticketForSessionFromDB.getSession().getName()).isEqualTo(session2.getName());
        assertThat(ticketForSessionFromDB.getPlace().getId()).isEqualTo(place2.getId());
        assertThat(ticketForSessionFromDB.getPlace().getRow()).isEqualTo(place2.getRow());
        assertThat(ticketForSessionFromDB.getPlace().getCell()).isEqualTo(place2.getCell());
    }

    @Test
    public void whenFindAllPlacesFromTicketsBySessionId() {
        TicketRepository ticketStore = new TicketRepository(pool);
        UsersRepository userStore = new UsersRepository(pool);
        SessionsRepository sessionStore = new SessionsRepository(pool);
        PlaceRepository placeStore = new PlaceRepository(pool);
        User user = new User("Ivan", "123@mail", "2-00-00");
        userStore.addUser(user);
        Session session1 = new Session("Taxi");
        Session session2 = new Session("Blade");
        sessionStore.addSession(session1);
        sessionStore.addSession(session2);
        Place place1 = placeStore.findById(2).get();
        Place place2 = placeStore.findById(3).get();
        Ticket ticket1 = new Ticket(session1, user, place1);
        Ticket ticket2 = new Ticket(session2, user, place2);
        ticketStore.add(ticket1);
        ticketStore.add(ticket2);
        List<Place> placesFromDB =
                ticketStore.findAllPlacesFromTicketsBySessionId(session2.getId());
        assertThat(placesFromDB.size()).isEqualTo(1);
        assertThat(placesFromDB.get(0).getId()).isEqualTo(place2.getId());
        assertThat(placesFromDB.get(0).getRow()).isEqualTo(place2.getRow());
        assertThat(placesFromDB.get(0).getCell()).isEqualTo(place2.getCell());
    }
}