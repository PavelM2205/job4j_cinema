package ru.job4j.persistence;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.models.Session;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class SessionsDBStore {
    private final BasicDataSource pool;
    private static final Logger LOG = LoggerFactory.getLogger(SessionsDBStore.class);

    public SessionsDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public Session addSession(Session session) {
        try (PreparedStatement st = pool.getConnection().prepareStatement(
                "INSERT INTO sessions(name) VALUES (?) ON CONFLICT DO NOTHING",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            st.setString(1, session.getName());
            st.execute();
            try (ResultSet res = st.getGeneratedKeys()) {
                if (res.next()) {
                    session.setId(res.getInt("id"));
                }
            }
        } catch (Exception exc) {
            LOG.error("Exception: ", exc);
        }
        return session;
    }

    public Optional<Session> findById(int id) {
        Optional<Session> result = Optional.empty();
        try (PreparedStatement st = pool.getConnection().prepareStatement(
                "SELECT * FROM sessions WHERE id = ?")) {
            st.setInt(1, id);
            try (ResultSet res = st.executeQuery()) {
                if (res.next()) {
                    result = Optional.of(new Session(
                            res.getInt("id"),
                            res.getString("name")
                    ));
                }
            }
        } catch (Exception exc) {
            LOG.error("Exception: ", exc);
        }
        return result;
    }

    public boolean updateSession(Session session) {
        boolean result = false;
        try (PreparedStatement st = pool.getConnection().prepareStatement(
                "UPDATE sessions SET name = ? WHERE id = ?")) {
            st.setString(1, session.getName());
            st.setInt(2, session.getId());
            result = st.executeUpdate() > 0;
        } catch (Exception exc) {
            LOG.error("Exception: ", exc);
        }
        return result;
    }

    public List<Session> findAll() {
        List<Session> sessions = new ArrayList<>();
        try (PreparedStatement st = pool.getConnection().prepareStatement(
                "SELECT * FROM sessions")) {
            try (ResultSet res = st.executeQuery()) {
                while (res.next()) {
                    sessions.add(new Session(
                            res.getInt("id"),
                            res.getString("name")
                    ));
                }
            }
        } catch (Exception exc) {
            LOG.error("Exception: ", exc);
        }
        return sessions;
    }
}
