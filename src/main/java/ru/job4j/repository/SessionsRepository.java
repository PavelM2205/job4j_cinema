package ru.job4j.repository;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.model.Session;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class SessionsRepository {
    private static final Logger LOG = LoggerFactory.getLogger(SessionsRepository.class);
    private static final String INSERT_SESSION =
            "INSERT INTO sessions(name) VALUES (?) ON CONFLICT DO NOTHING";
    private static final String SELECT_SESSION_BY_ID = "SELECT * FROM sessions WHERE id = ?";
    private static final String UPDATE_SESSION = "UPDATE sessions SET name = ? WHERE id = ?";
    private static final String SELECT_ALL_SESSIONS = "SELECT * FROM sessions";
    private final BasicDataSource pool;

    public SessionsRepository(BasicDataSource pool) {
        this.pool = pool;
    }

    public Session addSession(Session session) {
        try (PreparedStatement st = pool.getConnection().prepareStatement(
                INSERT_SESSION, PreparedStatement.RETURN_GENERATED_KEYS)) {
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
        try (PreparedStatement st = pool.getConnection().prepareStatement(SELECT_SESSION_BY_ID)) {
            st.setInt(1, id);
            try (ResultSet res = st.executeQuery()) {
                if (res.next()) {
                    result = Optional.of(getSessionFromResultSet(res));
                }
            }
        } catch (Exception exc) {
            LOG.error("Exception: ", exc);
        }
        return result;
    }

    public boolean updateSession(Session session) {
        boolean result = false;
        try (PreparedStatement st = pool.getConnection().prepareStatement(UPDATE_SESSION)) {
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
        try (PreparedStatement st = pool.getConnection().prepareStatement(SELECT_ALL_SESSIONS)) {
            try (ResultSet res = st.executeQuery()) {
                while (res.next()) {
                    sessions.add(getSessionFromResultSet(res));
                }
            }
        } catch (Exception exc) {
            LOG.error("Exception: ", exc);
        }
        return sessions;
    }

    private Session getSessionFromResultSet(ResultSet res) throws SQLException {
        return new Session(
                res.getInt("id"),
                res.getString("name")
        );
    }
}
