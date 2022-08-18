package ru.job4j.persistence;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.Main;
import ru.job4j.models.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

public class UsersDBStoreTest {
    private static BasicDataSource pool;

    @BeforeAll
    public static void loadPool() {
        pool = new Main().loadPool();
    }

    @AfterEach
    public void cleanTable() throws SQLException {
        try (PreparedStatement statement = pool.getConnection()
                .prepareStatement("DELETE FROM users")) {
            statement.execute();
        }
    }

    @Test
    public void whenAddUser() {
        UsersDBStore store = new UsersDBStore(pool);
        User user = new User("Ivan", "123@mail", "2-00-00");
        store.addUser(user);
        User userFromDb = store.findById(user.getId()).get();
        assertThat(userFromDb.getUsername()).isEqualTo(user.getUsername());
        assertThat(userFromDb.getEmail()).isEqualTo(user.getEmail());
        assertThat(userFromDb.getPhone()).isEqualTo(user.getPhone());
    }

    @Test
    public void whenUpdateThenMustBeChangedUserWithSameId() {
        UsersDBStore store = new UsersDBStore(pool);
        User user = new User("Pavel", "456@mail", "3-00-00");
        store.addUser(user);
        User changed = new User(
                user.getId(), "Egor", "789@mail", "8-00-00");
        store.updateUser(changed);
        User userFromDB = store.findById(user.getId()).get();
        assertThat(userFromDB.getUsername()).isEqualTo(changed.getUsername());
        assertThat(userFromDB.getEmail()).isEqualTo(changed.getEmail());
        assertThat(userFromDB.getPhone()).isEqualTo(changed.getPhone());
    }

    @Test
    public void whenAddUserThenMustBeInstallIdIntoUser() {
        UsersDBStore store = new UsersDBStore(pool);
        User user = new User("Maxim", "abc@mail", "4-00-00");
        store.addUser(user);
        assertThat(user.getId()).isNotEqualTo(0);
    }

    @Test
    public void whenAddTwoUsersThenFindAllReturnsBoth() {
        UsersDBStore store = new UsersDBStore(pool);
        User user1 = new User("Andrey", "vbn@mail", "5-00-00");
        User user2 = new User("Alexey", "jkl@mail", "6-00-00");
        store.addUser(user1);
        store.addUser(user2);
        List<User> expected = List.of(user1, user2);
        assertThat(store.findAll()).isEqualTo(expected);
    }

    @Test
    public void whenUserIsNotIntoDBThenMustBeEmptyOptional() {
        UsersDBStore store = new UsersDBStore(pool);
        assertThat(store.findById(1)).isEqualTo(Optional.empty());
    }
}