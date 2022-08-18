package ru.job4j.service;

import ru.job4j.models.Session;
import ru.job4j.persistence.SessionsDBStore;

import java.util.List;
import java.util.Optional;

public class SessionService {
    private final SessionsDBStore store;

    public SessionService(SessionsDBStore store) {
        this.store = store;
    }

    public Session add(Session session) {
        return store.addSession(session);
    }

    public Optional<Session> findById(int id) {
        return store.findById(id);
    }

    public boolean update(Session session) {
        return store.updateSession(session);
    }

    public List<Session> findAll() {
        return store.findAll();
    }
}
