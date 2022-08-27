package ru.job4j.service;

import org.springframework.stereotype.Service;
import ru.job4j.models.Place;
import ru.job4j.models.Session;
import ru.job4j.persistence.SessionsDBStore;
import ru.job4j.persistence.TicketDBStore;

import java.util.*;

@Service
public class SessionService {
    private final SessionsDBStore sessionStore;
    private final TicketDBStore ticketStore;
    private static final int MAX_ROW = 7;
    private static final int MAX_CELL = 6;

    public SessionService(SessionsDBStore sessionStore, TicketDBStore ticketStore) {
        this.sessionStore = sessionStore;
        this.ticketStore = ticketStore;
    }

    public Session add(Session session) {
        return sessionStore.addSession(session);
    }

    public Optional<Session> findById(int id) {
        return sessionStore.findById(id);
    }

    public boolean update(Session session) {
        return sessionStore.updateSession(session);
    }

    public List<Session> findAll() {
        return sessionStore.findAll();
    }

    public List<Place> getFreePlaces(int sessionId) {
        List<Place> freePlaces = getAllPlaces();
        ticketStore.findAllTicketsForSomeSession(sessionId)
                .stream()
                .map(ticket -> new Place(0, ticket.getRow(), ticket.getCell()))
                .forEach(place -> {
                    for (var el : freePlaces) {
                        if (el.equals(place)) {
                            el.setTaken(true);
                        }
                    }
                });
        return freePlaces;
    }

    public Place findPlaceById(int id) {
        return getAllPlaces().get(id);
    }

    public List<Place> getAllPlaces() {
        List<Place> result = new ArrayList<>();
        int id = 0;
        for (int row = 1; row <= MAX_ROW; row++) {
            for (int cell = 1; cell <= MAX_CELL; cell++) {
                result.add(new Place(id, row, cell));
                id++;
            }
        }
        return result;
    }
}
