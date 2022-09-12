package ru.job4j.repository;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.Main;
import ru.job4j.model.Session;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

public class SessionsRepositoryTest {
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
        SessionsRepository store = new SessionsRepository(pool);
        Session session = new Session("Белое солнце пустыни");
        store.addSession(session);
        Session sessionFromDB = store.findById(session.getId()).get();
        assertThat(sessionFromDB.getName()).isEqualTo(session.getName());
    }

    @Test
    public void whenUpdateSessionThenMustBeChangedWithSameId() {
        SessionsRepository store = new SessionsRepository(pool);
        Session session = new Session("Batman");
        store.addSession(session);
        Session changed = new Session(session.getId(), "Batman 2");
        store.updateSession(changed);
        Session sessionFromDB = store.findById(session.getId()).get();
        assertThat(sessionFromDB.getName()).isEqualTo(changed.getName());
    }

    @Test
    public void whenAddTwoSessionsThenFindAllReturnsBoth() {
        SessionsRepository store = new SessionsRepository(pool);
        Session session1 = new Session("Taxi");
        Session session2 = new Session("Blade");
        store.addSession(session1);
        store.addSession(session2);
        assertThat(store.findAll()).containsExactlyInAnyOrder(session1, session2);
    }

    @Test
    public void whenAddSessionThenMustBeInstallIdIntoIt() {
        SessionsRepository store = new SessionsRepository(pool);
        Session session = new Session("Blade");
        store.addSession(session);
        assertThat(session.getId()).isNotEqualTo(0);
    }

    @Test
    public void whenSessionIsNotIntoStoreThenFindByIdReturnsEmptyOptional() {
        SessionsRepository store = new SessionsRepository(pool);
        assertThat(store.findById(0)).isEqualTo(Optional.empty());
    }

    @Test
    public void whenAddTwoSessionsWithSameNameThenSecondAddReturnsOptionalEmpty() {
        SessionsRepository store = new SessionsRepository(pool);
        Session session1 = new Session("Taxi");
        Session session2 = new Session("Taxi");
        assertThat(store.addSession(session1).isPresent()).isTrue();
        assertThat(store.addSession(session2).isEmpty()).isTrue();
    }
}