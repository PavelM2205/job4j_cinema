package ru.job4j.persistence;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.Main;
import ru.job4j.models.Session;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

public class SessionsDBStoreTest {
    private static BasicDataSource pool;

    @BeforeAll
    public static void loadPool() {
        pool = new Main().loadPool();
    }

    @AfterEach
    public void cleanTable() throws SQLException {
        try (PreparedStatement st = pool.getConnection()
                .prepareStatement("DELETE FROM sessions")) {
            st.execute();
        }
    }

    @Test
    public void whenAddSession() {
        SessionsDBStore store = new SessionsDBStore(pool);
        Session session = new Session("Белое солнце пустыни");
        store.addSession(session);
        Session sessionFromDB = store.findById(session.getId()).get();
        assertThat(sessionFromDB.getName()).isEqualTo(session.getName());
    }

    @Test
    public void whenUpdateSessionThenMustBeChangedWithSameId() {
        SessionsDBStore store = new SessionsDBStore(pool);
        Session session = new Session("Batman");
        store.addSession(session);
        Session changed = new Session(session.getId(), "Batman 2");
        store.updateSession(changed);
        Session sessionFromDB = store.findById(session.getId()).get();
        assertThat(sessionFromDB.getName()).isEqualTo(changed.getName());
    }

    @Test
    public void whenAddTwoSessionsThenFindAllReturnsBoth() {
        SessionsDBStore store = new SessionsDBStore(pool);
        Session session1 = new Session("Taxi");
        Session session2 = new Session("Blade");
        store.addSession(session1);
        store.addSession(session2);
        List<Session> excepted = List.of(session1, session2);
        assertThat(store.findAll()).isEqualTo(excepted);
    }

    @Test
    public void whenAddSessionThenMustBeInstallIdIntoIt() {
        SessionsDBStore store = new SessionsDBStore(pool);
        Session session = new Session("Blade");
        store.addSession(session);
        assertThat(session.getId()).isNotEqualTo(0);
    }

    @Test
    public void whenSessionInNotIntoStoreThenFindByIdReturnsEmptyOptional() {
        SessionsDBStore store = new SessionsDBStore(pool);
        assertThat(store.findById(1)).isEqualTo(Optional.empty());
    }
}