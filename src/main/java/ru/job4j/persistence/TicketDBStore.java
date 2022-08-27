package ru.job4j.persistence;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.models.Session;
import ru.job4j.models.Ticket;
import ru.job4j.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@Repository
public class TicketDBStore {
    private static final Logger LOG = LoggerFactory.getLogger(TicketDBStore.class);
    private final BasicDataSource pool;

    public TicketDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public Optional<Ticket> add(Ticket ticket) {
        Optional<Ticket> result = Optional.empty();
        try (PreparedStatement st = pool.getConnection().prepareStatement(
                "INSERT INTO ticket (session_id, user_id, pos_row, cell) VALUES (?, ?, ?, ?)"
                + "ON CONFLICT DO NOTHING",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            st.setInt(1, ticket.getSession().getId());
            st.setInt(2, ticket.getUser().getId());
            st.setInt(3, ticket.getRow());
            st.setInt(4, ticket.getCell());
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
                "SELECT * FROM ticket WHERE id = ?")) {
            st.setInt(1, id);
            try (ResultSet res = st.executeQuery()) {
                if (res.next()) {
                    result = Optional.of(new Ticket(
                            res.getInt("id"),
                            new Session(res.getInt("session_id")),
                            new User(res.getInt("user_id")),
                            res.getInt("pos_row"),
                            res.getInt("cell")
                    ));
                }
            }
        } catch (Exception exc) {
            LOG.error("Exception: ", exc);
        }
        return result;
    }

    public boolean update(Ticket ticket) {
        boolean result = false;
        try (PreparedStatement st = pool.getConnection().prepareStatement(
                "UPDATE ticket SET session_id = ?, user_id = ?, pos_row = ?, cell = ?")) {
            st.setInt(1, ticket.getSession().getId());
            st.setInt(2, ticket.getUser().getId());
            st.setInt(3, ticket.getRow());
            st.setInt(4, ticket.getCell());
            result = st.executeUpdate() > 0;
        } catch (Exception exc) {
            LOG.error("Exception: ", exc);
        }
        return result;
    }

    public List<Ticket> findAll() {
        List<Ticket> result = new ArrayList<>();
        try (PreparedStatement st = pool.getConnection().prepareStatement(
                "SELECT * FROM ticket")) {
            try (ResultSet res = st.executeQuery()) {
                while (res.next()) {
                    result.add(new Ticket(
                        res.getInt("id"),
                        new Session(res.getInt("session_id")),
                        new User(res.getInt("user_id")),
                        res.getInt("pos_row"),
                        res.getInt("cell")
                    ));
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
                "SELECT * FROM ticket WHERE session_id = ?")) {
            st.setInt(1, sessionId);
            try (ResultSet res = st.executeQuery()) {
                while (res.next()) {
                    result.add(new Ticket(
                            res.getInt("id"),
                            res.getInt("pos_row"),
                            res.getInt("cell")
                    ));
                }
            }
        } catch (Exception exc) {
            LOG.error("Exception: ", exc);
        }
        return result;
    }
}
