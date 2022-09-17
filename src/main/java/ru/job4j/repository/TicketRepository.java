package ru.job4j.repository;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.model.Place;
import ru.job4j.model.Session;
import ru.job4j.model.Ticket;
import ru.job4j.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class TicketRepository {
    private static final Logger LOG = LoggerFactory.getLogger(TicketRepository.class);
    private static final String INSERT_TICKET =
            "INSERT INTO ticket (session_id, user_id, place_id) VALUES (?, ?, ?)";
    private static final String SELECT_TICKET_BY_ID = """
            SELECT t.id, t.place_id, p.pos_row, p.cell, t.session_id, s.name, t.user_id,
            u.username, u.email, u.phone
            FROM ticket AS t
            JOIN sessions AS s
            ON t.session_id = s.id
            JOIN users AS u
            ON t.user_id = u.id
            JOIN places as p
            ON t.place_id = p.id
            WHERE t.id = ?""";
    private static final String UPDATE_TICKET =
            "UPDATE ticket SET session_id = ?, user_id = ?, place_id = ?";
    private static final String SELECT_ALL_TICKETS = """
            SELECT t.id, t.place_id, p.pos_row, p.cell, t.session_id, s.name,
            t.user_id, u.username, u.email, u.phone
            FROM ticket AS t
            JOIN sessions AS s
            ON t.session_id = s.id
            JOIN users AS u
            ON t.user_id = u.id
            JOIN places as p
            ON t.place_id = p.id""";
    private static final String SELECT_TICKETS_BY_SESSION_ID = """
            SELECT t.id, t.place_id, p.pos_row, p.cell, t.session_id, s.name,
            t.user_id, u.username, u.email, u.phone
            FROM ticket AS t
            JOIN sessions AS s
            ON t.session_id = s.id
            JOIN users AS u
            ON t.user_id = u.id
            JOIN places as p
            ON t.place_id = p.id
            WHERE s.id = ?""";
    private static final String SELECT_ALL_PLACES_FROM_TICKETS_BY_SESSION_ID = """
            SELECT t.place_id, p.pos_row, p.cell
            FROM ticket AS t
            JOIN places AS p
            ON t.place_id = p.id
            WHERE t.session_id = ?""";
    private final BasicDataSource pool;

    public TicketRepository(BasicDataSource pool) {
        this.pool = pool;
    }

    public Optional<Ticket> add(Ticket ticket) {
        Optional<Ticket> result = Optional.empty();
        try (PreparedStatement st = pool.getConnection().prepareStatement(
                INSERT_TICKET, PreparedStatement.RETURN_GENERATED_KEYS)) {
            st.setInt(1, ticket.getSession().getId());
            st.setInt(2, ticket.getUser().getId());
            st.setInt(3, ticket.getPlace().getId());
            st.execute();
            try (ResultSet res = st.getGeneratedKeys()) {
                if (res.next()) {
                    ticket.setId(res.getInt("id"));
                    result = Optional.of(ticket);
                }
            }
        } catch (Exception exc) {
            LOG.error("Exception: ", exc);
        }
        return result;
    }

    public Optional<Ticket> findById(int id) {
        Optional<Ticket> result = Optional.empty();
        try (PreparedStatement st = pool.getConnection().prepareStatement(
                SELECT_TICKET_BY_ID)) {
            st.setInt(1, id);
            try (ResultSet res = st.executeQuery()) {
                if (res.next()) {
                    result = Optional.of(getTicketFromResultSet(res));
                }
            }
        } catch (Exception exc) {
            LOG.error("Exception: ", exc);
        }
        return result;
    }

    public boolean update(Ticket ticket) {
        boolean result = false;
        try (PreparedStatement st = pool.getConnection().prepareStatement(UPDATE_TICKET)) {
            st.setInt(1, ticket.getSession().getId());
            st.setInt(2, ticket.getUser().getId());
            st.setInt(3, ticket.getPlace().getId());
            result = st.executeUpdate() > 0;
        } catch (Exception exc) {
            LOG.error("Exception: ", exc);
        }
        return result;
    }

    public List<Ticket> findAll() {
        List<Ticket> result = new ArrayList<>();
        try (PreparedStatement st = pool.getConnection().prepareStatement(SELECT_ALL_TICKETS)) {
            try (ResultSet res = st.executeQuery()) {
                while (res.next()) {
                    result.add(getTicketFromResultSet(res));
                }
            }
        } catch (Exception exc) {
            LOG.error("Exception: ", exc);
        }
        return result;
    }

    public List<Ticket> findAllTicketsForSomeSession(int sessionId) {
        List<Ticket> result = new ArrayList<>();
        try (PreparedStatement st = pool.getConnection().prepareStatement(
                SELECT_TICKETS_BY_SESSION_ID)) {
            st.setInt(1, sessionId);
            try (ResultSet res = st.executeQuery()) {
                while (res.next()) {
                    result.add(getTicketFromResultSet(res));
                }
            }
        } catch (Exception exc) {
            LOG.error("Exception: ", exc);
        }
        return result;
    }

    private Ticket getTicketFromResultSet(ResultSet res) throws SQLException {
        return new Ticket(
                res.getInt("id"),
                new Session(
                        res.getInt("session_id"),
                        res.getString("name")
                ),
                new User(
                        res.getInt("user_id"),
                        res.getString("username"),
                        res.getString("email"),
                        res.getString("phone")
                ),
                new Place(
                        res.getInt("place_id"),
                        res.getInt("pos_row"),
                        res.getInt("cell")
                )
        );
    }

    public List<Place> findAllPlacesFromTicketsBySessionId(int id) {
        List<Place> result = new ArrayList<>();
        try (PreparedStatement st = pool.getConnection().prepareStatement(
                SELECT_ALL_PLACES_FROM_TICKETS_BY_SESSION_ID)) {
            st.setInt(1, id);
            try (ResultSet res = st.executeQuery()) {
                while (res.next()) {
                    result.add(
                            new Place(
                                    res.getInt("place_id"),
                                    res.getInt("pos_row"),
                                    res.getInt("cell")
                            ));
                }
            }
        } catch (Exception exc) {
            LOG.error("Exception when getAllPlacesFromTickets from DB: ", exc);
        }
        return result;
    }
}
