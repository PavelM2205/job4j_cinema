package ru.job4j.service;

import org.springframework.stereotype.Service;
import ru.job4j.model.Place;
import ru.job4j.model.Session;
import ru.job4j.repository.SessionsRepository;
import ru.job4j.repository.TicketRepository;

import java.util.*;

@Service
public class SessionService {
    private final SessionsRepository sessionStore;
    private final TicketRepository ticketStore;

    public SessionService(SessionsRepository sessionStore, TicketRepository ticketStore) {
        this.sessionStore = sessionStore;
        this.ticketStore = ticketStore;
    }

    public Session add(Session session) {
        Optional<Session> optSession = sessionStore.addSession(session);
        if (optSession.isEmpty()) {
            throw new IllegalStateException("Session was not added");
        }
        return optSession.get();
    }

    public Session findById(int id) {
        Optional<Session> optSession = sessionStore.findById(id);
        if (optSession.isEmpty()) {
            throw new IllegalArgumentException("Session with such id does not exists");
        }
        return optSession.get();
    }

    public boolean update(Session session) {
        return sessionStore.updateSession(session);
    }

    public List<Session> findAll() {
        return sessionStore.findAll();
    }

    public HashMap<Integer, Place> getFreePlaces(int sessionId) {
        HashMap<Integer, Place> freePlaces = new HashMap<>();
        ticketStore.findAllPlacesFromTicketsBySessionId(sessionId)
                .forEach(place -> freePlaces.put(place.getId(), place));
        return freePlaces;
    }
}
