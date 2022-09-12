package ru.job4j.repository;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UsersRepository {
    private static final Logger LOG = LoggerFactory.getLogger(UsersRepository.class);
    private static final String SELECT_USER = """
            INSERT INTO users(username, email, phone)
            VALUES (?, ?, ?)""";
    private static final String SELECT_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String UPDATE_USER =
            "UPDATE users SET username = ?, email = ?, phone = ? WHERE id = ?";
    private static final String SELECT_ALL_USERS = "SELECT * FROM users";
    private static final String SELECT_USER_BY_EMAIL_AND_PHONE =
            "SELECT * FROM users WHERE email = ? AND phone = ?";

    private final BasicDataSource pool;


    public UsersRepository(BasicDataSource pool) {
        this.pool = pool;
    }

    public Optional<User> addUser(User user) {
        Optional<User> result  = Optional.empty();
        try (PreparedStatement statement = pool.getConnection().prepareStatement(
                SELECT_USER, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPhone());
            statement.execute();
            try (ResultSet res = statement.getGeneratedKeys()) {
                if (res.next()) {
                    user.setId(res.getInt("id"));
                    result = Optional.of(user);
                }
            }
        } catch (SQLIntegrityConstraintViolationException exc) {
            return Optional.empty();
        } catch (Exception exc) {
            LOG.error("Exception: ", exc);
        }
        return result;
    }

    public Optional<User> findById(int id) {
        Optional<User> result = Optional.empty();
        try (PreparedStatement statement = pool.getConnection().prepareStatement(
                SELECT_USER_BY_ID)) {
            statement.setInt(1, id);
            try (ResultSet res = statement.executeQuery()) {
                if (res.next()) {
                    result = Optional.of(getUserFromResultSet(res));
                }
            }
        } catch (Exception exc) {
            LOG.error("Exception: ", exc);
        }
        return result;
    }

    public boolean updateUser(User user) {
        boolean result = false;
        try (PreparedStatement statement = pool.getConnection().prepareStatement(UPDATE_USER)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPhone());
            statement.setInt(4, user.getId());
            result = statement.executeUpdate() > 0;
        } catch (Exception exc) {
            LOG.error("Exception: ", exc);
        }
        return result;
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (PreparedStatement statement = pool.getConnection().prepareStatement(
                SELECT_ALL_USERS)) {
            try (ResultSet res = statement.executeQuery()) {
                while (res.next()) {
                    users.add(getUserFromResultSet(res));
                }
            }
        } catch (Exception exc) {
            LOG.error("Exception: ", exc);
        }
        return users;
    }

    public Optional<User> findUserByEmailAndPhone(String email, String phone) {
        Optional<User> result = Optional.empty();
        try (PreparedStatement st = pool.getConnection().prepareStatement(
                SELECT_USER_BY_EMAIL_AND_PHONE)) {
            st.setString(1, email);
            st.setString(2, phone);
            try (ResultSet res = st.executeQuery()) {
                if (res.next()) {
                    result = Optional.of(getUserFromResultSet(res));
                }
            }
        } catch (Exception exc) {
            LOG.error("Exception: ", exc);
        }
        return result;
    }

    private User getUserFromResultSet(ResultSet res) throws SQLException {
        return new User(
                res.getInt("id"),
                res.getString("username"),
                res.getString("email"),
                res.getString("phone")
        );
    }
}
