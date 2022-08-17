package ru.job4j.persistence;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsersDBStore {
    private final BasicDataSource pool;
    private static final Logger LOG = LoggerFactory.getLogger(UsersDBStore.class);

    public UsersDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public User addUser(User user) {
        try (PreparedStatement statement = pool.getConnection().prepareStatement(
                "INSERT INTO users(username, email, phone) VALUES (?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPhone());
            statement.execute();
            try (ResultSet res = statement.getGeneratedKeys()) {
                if (res.next()) {
                    user.setId(res.getInt("id"));
                }
            }
        } catch (Exception exc) {
            LOG.error("Exception: ", exc);
        }
        return user;
    }

    public Optional<User> findById(int id) {
        Optional<User> result = Optional.empty();
        try (PreparedStatement statement = pool.getConnection().prepareStatement(
                "SELECT * FROM users WHERE id = ?")) {
            statement.setInt(1, id);
            try (ResultSet res = statement.executeQuery()) {
                if (res.next()) {
                    result = Optional.of(new User(
                            res.getInt("id"),
                            res.getString("username"),
                            res.getString("email"),
                            res.getString("phone")
                    ));
                }
            }
        } catch (Exception exc) {
            LOG.error("Exception: ", exc);
        }
        return result;
    }

    public boolean updateUser(User user) {
        boolean result = false;
        try (PreparedStatement statement = pool.getConnection().prepareStatement(
                "UPDATE users SET username = ?, email = ?, phone = ? WHERE id = ?")) {
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
                "SELECT * FROM users")) {
            try (ResultSet res = statement.executeQuery()) {
                while (res.next()) {
                    users.add(new User(
                            res.getInt("id"),
                            res.getString("username"),
                            res.getString("email"),
                            res.getString("phone")
                    ));
                }
            }
        } catch (Exception exc) {
            LOG.error("Exception: ", exc);
        }
        return users;
    }
}
